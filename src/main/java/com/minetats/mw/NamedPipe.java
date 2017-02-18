/*
 * Copyright 2017 The MFoD Project
 * 
 * The MFoD Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * Copyright 2014 The Netty Project
 * 
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.minetats.mw;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedInput;

public class NamedPipe implements ChunkedInput<ByteBuf> {

	  private RandomAccessFile file;
	  private int chunkSize;
	  private boolean endOfInput;
	  private int chunks;

	  public NamedPipe(File file, int chunkSize) throws IOException {
	    this(new RandomAccessFile(file, "r"), chunkSize);
	  }

	  public NamedPipe(File file) throws IOException {
	    this(new RandomAccessFile(file, "r"), 8192);
	  }

	  public NamedPipe(RandomAccessFile raf) throws IOException {
	    this(raf, 8192);
	  }

	  public NamedPipe(RandomAccessFile file, int chunkSize) throws IOException {
	    if (file == null) {
	      throw new NullPointerException("file");
	    }
	    if (chunkSize <= 0) {
	      throw new IllegalArgumentException("chunkSize: " + chunkSize + " (expected: a positive integer)");
	    }

	    this.file = file;
	    this.chunkSize = chunkSize;
	  }

	  @Override
	  public boolean isEndOfInput() throws Exception {
	    return endOfInput;
	  }

	  @Override
	  public void close() throws Exception {
	    file.close();
	    System.out.println("input closed");
	  }

	  @Override
	  public ByteBuf readChunk(ByteBufAllocator bba) throws Exception {
	    chunks++;
	    ByteBuf buf = bba.heapBuffer(chunkSize);
	    boolean release = false;
	    int read = 0;
	    try {
	      do {
	        buf.writerIndex(buf.writerIndex() + read);
	        read = file.read(buf.array(), buf.arrayOffset() + read, chunkSize - read);
	      } while (read > 0);
	      int index = buf.writerIndex() - 1;
	      if (buf.getByte(index) == '\n' && buf.getByte(index - 1) == '\n') {
	        endOfInput = true;
	        System.out.println("endOfInput=" + endOfInput + ", read " + chunks + " chunks");
	      }
	      return buf;
	    } finally {
	      if (release) {
	        buf.release();
	      }
	    }
	  }

	  @Override
	  public ByteBuf readChunk(ChannelHandlerContext ctx) throws Exception {
		  return readChunk(ctx.alloc());
	  }

	  @Override
	  public long length() {
	    return 0;
	  }

	  @Override
	  public long progress() {
	    return 0;
	  }
	}
