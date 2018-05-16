package cn.ilumos.nio.channel;// $Id$

import java.nio.*;
/**
 * FloatBuffer存取数据
 * @author Lumos
 *
 */
public class UseFloatBuffer {
	static public void main(String args[]) throws Exception {
		FloatBuffer buffer = FloatBuffer.allocate(10);

		for (int i = 0; i < buffer.capacity(); ++i) {
			float f = (float) Math.sin((((float) i) / 10) * (2 * Math.PI));
			buffer.put(f);
		}
		//读写切换
		buffer.flip();

		while (buffer.hasRemaining()) {
			float f = buffer.get();
			System.out.println(f);
		}
	}
}
