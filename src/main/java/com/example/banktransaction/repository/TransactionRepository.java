package com.example.banktransaction.repository;

import java.util.List;
import java.util.Optional;

import com.example.banktransaction.model.PageResult;
import com.example.banktransaction.model.Transaction;
import com.example.banktransaction.model.TransactionQuery;

/**
 * 交易存储库接口
 * 定义交易数据访问的基本操作
 */
public interface TransactionRepository {
    
    /**
     * 保存交易记录
     * 
     * @param transaction 交易记录
     * @return 保存后的交易记录
     */
    Transaction save(Transaction transaction);
    
    /**
     * 根据ID查询交易记录
     * 
     * @param id 交易ID
     * @return 交易记录
     */
    Optional<Transaction> findById(String id);
    
    /**
     * 查询所有交易记录
     * 
     * @return 交易记录列表
     */
    List<Transaction> findAll();
    
    /**
     * 根据查询条件查询交易记录
     * 
     * @param query 查询条件
     * @return 分页结果
     */
    PageResult<Transaction> findByQuery(TransactionQuery query);
    
    /**
     * 删除交易记录
     * 
     * @param id 交易ID
     * @return 是否删除成功
     */
    boolean deleteById(String id);
    
    /**
     * 查询交易记录总数
     * 
     * @return 交易记录总数
     */
    long count();
} 