package cn.ilumos.nio.channel;// $Id$

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class MultiPortEcho {
	private int ports[];
	private ByteBuffer echoBuffer = ByteBuffer.allocate(1024);

	public MultiPortEcho(int ports[]) throws IOException {
		this.ports = ports;

		go();
	}

	private void go() throws IOException {
		// Create a new selector
		Selector selector = Selector.open();

		// Open a listener on each port, and register each one
		// with the selector
		for (int i = 0; i < ports.length; ++i) {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			//与Selector一起使用时，Channel必须处于非阻塞模式下。
			//这意味着不能将FileChannel与Selector一起使用，
			//因为FileChannel不能切换到非阻塞模式。而套接字通道都可以。
			ssc.configureBlocking(false);
			ServerSocket ss = ssc.socket();
			InetSocketAddress address = new InetSocketAddress(ports[i]);
			ss.bind(address);
			//注意register()方法的第二个参数。
			//这是一个“interest集合”，意思是在通过Selector监听Channel时对什么事件感兴趣。
			SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);

			System.out.println("Going to listen on " + ports[i]);
		}

		while (true) {
			//这个方法会阻塞，直到至少有一个已注册的事件发生
			int num = selector.select();
			//它返回发生了事件的 SelectionKey 对象的一个 集合
			Set selectedKeys = selector.selectedKeys();
			Iterator it = selectedKeys.iterator();

			while (it.hasNext()) {
				SelectionKey key = (SelectionKey) it.next();
				//readOps() 方法告诉我们该事件是新的连接。
				if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
					// Accept the new connection
					ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
					SocketChannel sc = ssc.accept();
					sc.configureBlocking(false);

					// Add the new connection to the selector
					SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
					it.remove();

					System.out.println("Got connection from " + sc);
				} else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
					// Read the data
					SocketChannel sc = (SocketChannel) key.channel();

					// Echo data
					int bytesEchoed = 0;
					while (true) {
						echoBuffer.clear();

						int r = sc.read(echoBuffer);

						if (r <= 0) {
							break;
						}

						echoBuffer.flip();

						sc.write(echoBuffer);
						bytesEchoed += r;
					}

					System.out.println("Echoed " + bytesEchoed + " from " + sc);

					it.remove();
				}

			}

			// System.out.println( "going to clear" );
			// selectedKeys.clear();
			// System.out.println( "cleared" );
		}
	}

	static public void main(String args[]) throws Exception {
		if (args.length <= 0) {
			System.err.println("Usage: java MultiPortEcho port [port port ...]");
			System.exit(1);
		}

		int ports[] = new int[args.length];

		for (int i = 0; i < args.length; ++i) {
			ports[i] = Integer.parseInt(args[i]);
		}

		new MultiPortEcho(ports);
	}
}
