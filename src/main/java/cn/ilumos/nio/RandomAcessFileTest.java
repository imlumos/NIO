package cn.ilumos.nio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Test;

/**
 * nio实现多线程下载
 * @author Lumos
 *
 */
public class RandomAcessFileTest {
	private File f=new File("e:\\test.avi");
	@Test
	public void download() throws IOException, InterruptedException{
		RandomAccessFile raf=new RandomAccessFile(f, "r");
		long length = raf.length();
		System.out.println(length);
//		raf.close();
		
		//计算每个线程下载的块的大小  
        int blockSize = (int) (length / 3);  
        System.out.println("文件大小为:"+blockSize);
        for(int i = 0; i <3; i++){  
            //每个线程的起始下载点  
            int startPos = blockSize * i;  
            //每个线程的结束下载点  
            int endPos = blockSize * (i + 1) - 1;  
            //如果是最后一条线程，将其下载的终止点设为文件的终点  
            if(i == 3){  
                endPos = (int) length;  
            }  
            System.out.println("线程" + i + "下载的部分为：" + startPos +"---" + endPos);  
            //开启线程分别下载不同的部分  
            new DownThread(i, startPos, endPos).start();  
        }  
		//主线程接触 并发线程也会结束 所以要等待
		Thread.sleep(999999);
	}
	
	
}
 class DownThread extends Thread{
	//线程ID  
    private int threadId;  
    //线程下载的起始点  
    private int startPos;  
    //线程下载的结束点  
    private int endPos;  
    private File f=new File("d:\\test.avi");
    public DownThread(int threadId, int startPos, int endPos) {  
        this.threadId = threadId;  
        this.startPos = startPos;  
        this.endPos = endPos;  
    }  

    @Override  
    public void run(){  
        try {  
        	RandomAccessFile rafR=new RandomAccessFile("e:\\test.avi", "r");
            int len = 0;  
            byte[] buf = new byte[1024];  
            //这里要注意新创建一个RandomAccessFile对象，而不能重复使用download方法中创建的  
            RandomAccessFile rafw = new RandomAccessFile("d:\\test.avi", "rwd");  
            rafw.setLength(rafR.length());
            //将写文件的指针指向下载的起始点  
        	rafR.seek(startPos); 
        	rafw.seek(startPos);
        	while((len = rafR.read(buf)) != -1 && rafR.getFilePointer()<=endPos){
        		System.out.println("线程"+threadId+"========="+rafR.getFilePointer());
				rafw.write(buf,0,len);
        	}
             
        	rafR.close();
        	rafw.close();
            System.out.println("线程" + threadId + "下载完毕...");  
        } catch (Exception e) {  
            e.printStackTrace();  
            
        }  
    }  
}
