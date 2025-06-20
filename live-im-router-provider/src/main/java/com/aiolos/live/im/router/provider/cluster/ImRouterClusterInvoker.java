package com.aiolos.live.im.router.provider.cluster;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.cluster.Directory;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;

import java.util.List;

public class ImRouterClusterInvoker<T> extends AbstractClusterInvoker<T> {

    public ImRouterClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    protected Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance) throws RpcException {
        checkWhetherDestroyed();
        String ip = (String) RpcContext.getContext().get("ip");
        if (StringUtils.isBlank(ip)) {
            throw new RuntimeException("ip is null");
        }
        // 获取到指定的rpc服务提供者的所有地址信息
        Invoker<T> matchInvoker = invokers.stream().filter(invoker -> {
            String serverIp = invoker.getUrl().getHost() + ":" + invoker.getUrl().getPort();
            return serverIp.equals(ip);
        }).findFirst().orElse(null);
        if (matchInvoker == null) {
            throw new RuntimeException("ip is invalid");
        }
        // 找到某一台机器调用live-im-core-server RouterHandlerRpc.sendMsg()
        return matchInvoker.invoke(invocation);
    }
}
