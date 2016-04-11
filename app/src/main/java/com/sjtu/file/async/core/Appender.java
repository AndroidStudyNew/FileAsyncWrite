package com.sjtu.file.async.core;

import java.util.LinkedList;

/**
 * ClassName: AsynFilewrite <br/>
 * Function: 用于异步的将文件写入文件，具体做法是先将写入缓冲区，再通过线程后台写入文件. <br/>
 * date: 2016-4-1 上午9:12:36 <br/>
 *
 * @author charles_zhu
 *
 */
public abstract class Appender<T> implements Runnable{

	public static final byte TAB = 0x09;
	public static final byte[] CRLF ={0x0d,0x0a};

	Thread mThread;
	int bufferSize = 0;
	LinkedList<T> mBuffer = new LinkedList<>();

	public Appender(int buffer) {
		if(buffer>0){
			bufferSize = buffer;
			mThread = new Thread(this,"Appender");
			mThread.setPriority(Thread.MIN_PRIORITY+2);
			mThread.start();
		}
	}

	abstract void append(T e);
	
	public void appendToQueue(T e){
		if (mThread == null) {
			append(e);
		} else {
			if(mBuffer.size() > bufferSize)//丢掉该消息
				return;
			synchronized (mBuffer) {
				mBuffer.add(e);
				mBuffer.notify();
			}
		}
	}
	public void close(){
		if(mThread!=null){
			alive = false;
			synchronized (mBuffer){
				mBuffer.clear();
				mBuffer.notify();
			}
			mThread = null;
		}
	}
	boolean alive = true;
	@Override
	public void run() {
		
		try {
			while (alive) {
				T ev =null;
				synchronized (mBuffer) {
					while (mBuffer.size() < 1) {
						if (alive)
							mBuffer.wait();
						else
							return;
					}
					ev = mBuffer.removeFirst();//  removeFirst 与 add 要线程安全
				}
				if(ev!=null)
					append(ev);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


}
