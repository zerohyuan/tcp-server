package com.example.tcp.server.runner;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class TcpServerRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group).channel(NioServerSocketChannel.class);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    // 增加字符串编码器
                    channel.pipeline()
                            .addLast(new StringEncoder(StandardCharsets.UTF_8))
                            .addLast(new StringDecoder(StandardCharsets.UTF_8))
                            .addLast(new PrintServer());
                }
            });
            ChannelFuture sync = bootstrap.bind(5678).sync();
            Channel channel = sync.channel();
            if (sync.isSuccess()) {
                log.info("server start success....");
            } else {
                log.error("server start fail....");
                sync.cause().printStackTrace();
            }
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("Failed to receive message: {}", e.getMessage());
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                log.error("Failed to close tcp, message: {}", e.getMessage());
            }
        }
    }

    private static class PrintServer extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            super.channelRead(ctx, msg);
            log.info("receive msg： {}", msg);
        }
    }
}
