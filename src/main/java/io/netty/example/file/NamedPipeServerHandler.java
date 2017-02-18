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

package io.netty.example.file;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;

import com.minetats.mw.NamedPipe;

public class NamedPipeServerHandler extends SimpleChannelInboundHandler<String> {

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    ctx.writeAndFlush("HELO: Type the path of the file to retrieve.\n");
  }

  @Override
  public void channelRead0(final ChannelHandlerContext ctx, String msg) throws Exception {
    RandomAccessFile raf = null;
    try {
      raf = new RandomAccessFile(msg, "r");
    } catch (Exception e) {
      ctx.writeAndFlush("ERR: " + e.getClass().getSimpleName() + ": " + e.getMessage() + '\n');
      raf.close();
      return;
    }
    ctx.writeAndFlush(new NamedPipe(raf, 512)).addListener(new ChannelFutureListener() {
      @Override
      public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
          System.out.println("writeAndFlush: operationComplete: failed, future.cause().getMessage()=" + future.cause().getMessage());
        } else {
          System.out.println("writeAndFlush: operationComplete: success");
        }
        ctx.close();
      }
    });
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();

    if (ctx.channel().isActive()) {
      ctx.writeAndFlush("ERR: " + cause.getClass().getSimpleName() + ": " + cause.getMessage() + '\n').addListener(
          ChannelFutureListener.CLOSE);
    }
  }
}
