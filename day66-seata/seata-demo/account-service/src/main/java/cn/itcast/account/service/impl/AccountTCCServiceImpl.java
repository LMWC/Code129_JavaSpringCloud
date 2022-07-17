package cn.itcast.account.service.impl;

import cn.itcast.account.entity.AccountFreeze;
import cn.itcast.account.mapper.AccountFreezeMapper;
import cn.itcast.account.mapper.AccountMapper;
import cn.itcast.account.service.AccountTCCService;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountTCCServiceImpl implements AccountTCCService {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountFreezeMapper accountFreezeMapper;

    @Override
    public void deduct(String userId, int money) {
        //当前全局事务的唯一标识XID
        String xid = RootContext.getXID();

        AccountFreeze accountFreeze = accountFreezeMapper.selectById(xid);
        if(accountFreeze!=null){
            //说明之前已经cancel过了，就不能再try了
            return;
        }

        //1.扣钱
        accountMapper.deduct(userId,money);
        //2.添加冻结金额
        AccountFreeze entity = new AccountFreeze();
        //当前全局事务的唯一标识XID
        entity.setXid(xid);
        entity.setUserId(userId);
        entity.setFreezeMoney(money);
        entity.setState(AccountFreeze.State.TRY);
        accountFreezeMapper.insert(entity);

    }

    @Override
    public boolean confirm(BusinessActionContext ctx) {

        // 删除 预留的资源记录

        int count = accountFreezeMapper.deleteById(ctx.getXid());

        return count==1;
    }

    @Override
    public boolean cancel(BusinessActionContext ctx) {
        String xid = ctx.getXid();
        //1.金额变成0
        AccountFreeze accountFreeze = accountFreezeMapper.selectById(xid);
        String userId = ctx.getActionContext("userId").toString();
        Integer money = Integer.parseInt(ctx.getActionContext("money").toString());
        if(accountFreeze==null){//没有try 不需要回滚
            AccountFreeze entity = new AccountFreeze();
            //当前全局事务的唯一标识XID
            entity.setXid(xid);
            entity.setUserId(userId);
            entity.setFreezeMoney(0);
            entity.setState(AccountFreeze.State.CANCEL);
            accountFreezeMapper.insert(entity);
            return true;
        }

        //幂等性判断
        if(accountFreeze.getState()==AccountFreeze.State.CANCEL){
            return true;
        }

        accountFreeze.setFreezeMoney(0);
        accountFreeze.setState(AccountFreeze.State.CANCEL);
        accountFreezeMapper.updateById(accountFreeze);
        //2.恢复数据
        accountMapper.refund(userId,money);
        //return false;
        return true;
    }
}
