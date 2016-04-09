/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.netty.channel;

import io.netty.util.internal.StringUtil;

import java.net.SocketAddress;

import static io.netty.channel.DefaultChannelPipeline.*;

/**
 * A set of helper methods for easier implementation of custom {@link ChannelHandlerInvoker} implementation.
 */
public final class ChannelHandlerInvokerUtil {

    /**
     * Makes best possible effort to detect if {@link ChannelHandler#handlerAdded(ChannelHandlerContext)} was called
     * yet. If not return {@code false} and if called or could not detect return {@code true}.
     *
     * If this method returns {@code true} we will not invoke the {@link ChannelHandler} but just forward the event.
     * This is needed as {@link DefaultChannelPipeline} may already put the {@link ChannelHandler} in the linked-list
     * but not called {@link }
     */
    private static boolean isAdded(ChannelHandlerContext ctx) {
        if (ctx instanceof AbstractChannelHandlerContext) {
            return ((AbstractChannelHandlerContext) ctx).isAdded();
        }
        return true;
    }

    public static void invokeChannelRegisteredNow(ChannelHandlerContext ctx) {
        if (isAdded(ctx)) {
            try {
                ((ChannelInboundHandler) ctx.handler()).channelRegistered(ctx);
            } catch (Throwable t) {
                notifyHandlerException(ctx, t);
            }
        } else {
            ctx.fireChannelRegistered();
        }
    }

    public static void invokeChannelUnregisteredNow(ChannelHandlerContext ctx) {
        if (isAdded(ctx)) {
            try {
                ((ChannelInboundHandler) ctx.handler()).channelUnregistered(ctx);
            } catch (Throwable t) {
                notifyHandlerException(ctx, t);
            }
        } else {
            ctx.fireChannelUnregistered();
        }
    }

    public static void invokeChannelActiveNow(final ChannelHandlerContext ctx) {
        if (isAdded(ctx)) {
            try {
                ((ChannelInboundHandler) ctx.handler()).channelActive(ctx);
            } catch (Throwable t) {
                notifyHandlerException(ctx, t);
            }
        } else {
            ctx.fireChannelActive();
        }
    }

    public static void invokeChannelInactiveNow(final ChannelHandlerContext ctx) {
        if (isAdded(ctx)) {
            try {
                ((ChannelInboundHandler) ctx.handler()).channelInactive(ctx);
            } catch (Throwable t) {
                notifyHandlerException(ctx, t);
            }
        } else {
            ctx.fireChannelInactive();
        }
    }

    public static void invokeExceptionCaughtNow(final ChannelHandlerContext ctx, final Throwable cause) {
        if (isAdded(ctx)) {
            try {
                ctx.handler().exceptionCaught(ctx, cause);
            } catch (Throwable t) {
                if (logger.isWarnEnabled()) {
                    logger.warn("An exception was thrown by a user handler's exceptionCaught() method:", t);
                    logger.warn(".. and the cause of the exceptionCaught() was:", cause);
                }
            }
        } else {
            ctx.fireExceptionCaught(cause);
        }
    }

    public static void invokeUserEventTriggeredNow(final ChannelHandlerContext ctx, final Object event) {
        if (isAdded(ctx)) {
            try {
                ((ChannelInboundHandler) ctx.handler()).userEventTriggered(ctx, event);
            } catch (Throwable t) {
                notifyHandlerException(ctx, t);
            }
        } else {
            ctx.fireUserEventTriggered(event);
        }
    }

    public static void invokeChannelReadNow(final ChannelHandlerContext ctx, final Object msg) {
        if (isAdded(ctx)) {
            try {
                ((ChannelInboundHandler) ctx.handler()).channelRead(ctx, msg);
            } catch (Throwable t) {
                notifyHandlerException(ctx, t);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    public static void invokeChannelReadCompleteNow(final ChannelHandlerContext ctx) {
        if (isAdded(ctx)) {
            try {
                ((ChannelInboundHandler) ctx.handler()).channelReadComplete(ctx);
            } catch (Throwable t) {
                notifyHandlerException(ctx, t);
            }
        } else {
            ctx.fireChannelReadComplete();
        }
    }

    public static void invokeChannelWritabilityChangedNow(final ChannelHandlerContext ctx) {
        if (isAdded(ctx)) {
            try {
                ((ChannelInboundHandler) ctx.handler()).channelWritabilityChanged(ctx);
            } catch (Throwable t) {
                notifyHandlerException(ctx, t);
            }
        } else {
            ctx.fireChannelWritabilityChanged();
        }
    }

    public static void invokeBindNow(
            final ChannelHandlerContext ctx, final SocketAddress localAddress, final ChannelPromise promise) {
        if (isAdded(ctx)) {
            try {
                ((ChannelOutboundHandler) ctx.handler()).bind(ctx, localAddress, promise);
            } catch (Throwable t) {
                notifyOutboundHandlerException(t, promise);
            }
        } else {
            ctx.bind(localAddress, promise);
        }
    }
    public static void invokeConnectNow(
            final ChannelHandlerContext ctx,
            final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
        if (isAdded(ctx)) {
            try {
                ((ChannelOutboundHandler) ctx.handler()).connect(ctx, remoteAddress, localAddress, promise);
            } catch (Throwable t) {
                notifyOutboundHandlerException(t, promise);
            }
        } else {
            ctx.connect(remoteAddress, localAddress, promise);
        }
    }

    public static void invokeDisconnectNow(final ChannelHandlerContext ctx, final ChannelPromise promise) {
        if (isAdded(ctx)) {
            try {
                ((ChannelOutboundHandler) ctx.handler()).disconnect(ctx, promise);
            } catch (Throwable t) {
                notifyOutboundHandlerException(t, promise);
            }
        } else {
            ctx.disconnect(promise);
        }
    }

    public static void invokeCloseNow(final ChannelHandlerContext ctx, final ChannelPromise promise) {
        if (isAdded(ctx)) {
            try {
                ((ChannelOutboundHandler) ctx.handler()).close(ctx, promise);
            } catch (Throwable t) {
                notifyOutboundHandlerException(t, promise);
            }
        } else {
            ctx.close(promise);
        }
    }

    public static void invokeDeregisterNow(final ChannelHandlerContext ctx, final ChannelPromise promise) {
        if (isAdded(ctx)) {
            try {
                ((ChannelOutboundHandler) ctx.handler()).deregister(ctx, promise);
            } catch (Throwable t) {
                notifyOutboundHandlerException(t, promise);
            }
        } else {
            ctx.deregister(promise);
        }
    }

    public static void invokeReadNow(final ChannelHandlerContext ctx) {
        if (isAdded(ctx)) {
            try {
                ((ChannelOutboundHandler) ctx.handler()).read(ctx);
            } catch (Throwable t) {
                notifyHandlerException(ctx, t);
            }
        } else {
            ctx.read();
        }
    }

    public static void invokeWriteNow(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (isAdded(ctx)) {
            try {
                ((ChannelOutboundHandler) ctx.handler()).write(ctx, msg, promise);
            } catch (Throwable t) {
                notifyOutboundHandlerException(t, promise);
            }
        } else {
            ctx.write(msg, promise);
        }
    }

    public static void invokeFlushNow(final ChannelHandlerContext ctx) {
        if (isAdded(ctx)) {
            try {
                ((ChannelOutboundHandler) ctx.handler()).flush(ctx);
            } catch (Throwable t) {
                notifyHandlerException(ctx, t);
            }
        } else {
            ctx.flush();
        }
    }

    public static boolean validatePromise(
            ChannelHandlerContext ctx, ChannelPromise promise, boolean allowVoidPromise) {
        if (ctx == null) {
            throw new NullPointerException("ctx");
        }

        if (promise == null) {
            throw new NullPointerException("promise");
        }

        if (promise.isDone()) {
            if (promise.isCancelled()) {
                return false;
            }
            throw new IllegalArgumentException("promise already done: " + promise);
        }

        if (promise.channel() != ctx.channel()) {
            throw new IllegalArgumentException(String.format(
                    "promise.channel does not match: %s (expected: %s)", promise.channel(), ctx.channel()));
        }

        if (promise.getClass() == DefaultChannelPromise.class) {
            return true;
        }

        if (!allowVoidPromise && promise instanceof VoidChannelPromise) {
            throw new IllegalArgumentException(
                    StringUtil.simpleClassName(VoidChannelPromise.class) + " not allowed for this operation");
        }

        if (promise instanceof AbstractChannel.CloseFuture) {
            throw new IllegalArgumentException(
                    StringUtil.simpleClassName(AbstractChannel.CloseFuture.class) + " not allowed in a pipeline");
        }
        return true;
    }

    private static void notifyHandlerException(ChannelHandlerContext ctx, Throwable cause) {
        if (inExceptionCaught(cause)) {
            if (logger.isWarnEnabled()) {
                logger.warn(
                        "An exception was thrown by a user handler " +
                                "while handling an exceptionCaught event", cause);
            }
            return;
        }

        invokeExceptionCaughtNow(ctx, cause);
    }

    private static void notifyOutboundHandlerException(Throwable cause, ChannelPromise promise) {
        if (!promise.tryFailure(cause) && !(promise instanceof VoidChannelPromise)) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to fail the promise because it's done already: {}", promise, cause);
            }
        }
    }

    private static boolean inExceptionCaught(Throwable cause) {
        do {
            StackTraceElement[] trace = cause.getStackTrace();
            if (trace != null) {
                for (StackTraceElement t : trace) {
                    if (t == null) {
                        break;
                    }
                    if ("exceptionCaught".equals(t.getMethodName())) {
                        return true;
                    }
                }
            }

            cause = cause.getCause();
        } while (cause != null);

        return false;
    }

    private ChannelHandlerInvokerUtil() { }
}
