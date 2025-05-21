package com.example.banktransaction.service;

import com.example.banktransaction.model.PageResult;
import com.example.banktransaction.model.Transaction;
import com.example.banktransaction.model.TransactionQuery;

/**
 * 交易服务接口
 * 定义交易业务逻辑的基本操作
 */
public interface TransactionService {
    
    /**
     * 创建交易记录
     * 
     * @param transaction 交易记录
     * @return 创建后的交易记录
     */
    Transaction createTransaction(Transaction transaction);
    
    /**
     * 更新交易记录
     * 
     * @param id 交易ID
     * @param transaction 交易记录
     * @return 更新后的交易记录
     */
    Transaction updateTransaction(String id, Transaction transaction);
    
    /**
     * 根据ID获取交易记录
     * 
     * @param id 交易ID
     * @return 交易记录
     */
    Transaction getTransaction(String id);
    
    /**
     * 删除交易记录
     * 
     * @param id 交易ID
     * @return 是否删除成功
     */
    boolean deleteTransaction(String id);
    
    /**
     * 查询交易记录
     * 
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<Transaction> queryTransactions(TransactionQuery query);
} 