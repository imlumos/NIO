package cn.ilumos.nio.channel;// $Id$

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
/**
 * 将文件映射到内存
 * @author Lumos
 *
 */
public class UseMappedFile {
	static private final int start = 0;
	static private final int size = 1024;

	static public void main(String args[]) throws Exception {
		RandomAccessFile raf = new RandomAccessFile("usemappedfile.txt", "rw");
		FileChannel fc = raf.getChannel();
		//map() 方法返回一个 MappedByteBuffer，它是 ByteBuffer 的子类。
		//因此，您可以像使用其他任何 ByteBuffer 一样使用新映射的缓冲区，操作系统会在需要时负责执行行映射。
		MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_WRITE, start, size);

		mbb.put(0, (byte) 97);
		mbb.put(2, (byte) 122);

		raf.close();
	}
}
