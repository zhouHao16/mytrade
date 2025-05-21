package com.example.banktransaction.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.banktransaction.model.PageResult;
import com.example.banktransaction.model.Transaction;
import com.example.banktransaction.model.TransactionQuery;
import com.example.banktransaction.service.TransactionService;

import lombok.RequiredArgsConstructor;

/**
 * 交易控制器
 * 处理Web页面请求
 */
@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    /**
     * 交易服务
     */
    private final TransactionService transactionService;
    
    /**
     * 显示交易列表页面
     */
    @GetMapping
    public String listTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        // 构建查询条件
        TransactionQuery query = TransactionQuery.builder()
                .startTime(startTime)
                .endTime(endTime)
                .type(type)
                .minAmount(minAmount)
                .maxAmount(maxAmount)
                .status(status)
                .page(page)
                .size(size)
                .build();
        
        // 查询交易记录
        PageResult<Transaction> pageResult = transactionService.queryTransactions(query);
        
        // 添加到模型
        model.addAttribute("pageResult", pageResult);
        model.addAttribute("query", query);
        
        return "transaction/list";
    }
    
    /**
     * 显示创建交易页面
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        // 创建空的交易记录，设置默认交易时间为当前时间
        Transaction transaction = new Transaction();
        transaction.setTransactionTime(LocalDateTime.now().withNano(0)); // 移除纳秒部分提高兼容性
        model.addAttribute("transaction", transaction);
        return "transaction/form";
    }
    
    /**
     * 处理创建交易请求
     */
    @PostMapping("/create")
    public String createTransaction(@Validated @ModelAttribute Transaction transaction,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        // 校验失败
        if (bindingResult.hasErrors()) {
            return "transaction/form";
        }
        
        // 创建交易记录
        transactionService.createTransaction(transaction);
        
        // 添加成功消息
        redirectAttributes.addFlashAttribute("successMessage", "交易记录创建成功");
        
        return "redirect:/transactions";
    }
    
    /**
     * 显示编辑交易页面
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        // 获取交易记录
        Transaction transaction = transactionService.getTransaction(id);
        
        // 添加到模型
        model.addAttribute("transaction", transaction);
        
        return "transaction/form";
    }
    
    /**
     * 处理编辑交易请求
     */
    @PostMapping("/edit/{id}")
    public String updateTransaction(@PathVariable String id,
                                  @Validated @ModelAttribute Transaction transaction,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        // 校验失败
        if (bindingResult.hasErrors()) {
            return "transaction/form";
        }
        
        // 更新交易记录
        transactionService.updateTransaction(id, transaction);
        
        // 添加成功消息
        redirectAttributes.addFlashAttribute("successMessage", "交易记录更新成功");
        
        return "redirect:/transactions";
    }
    
    /**
     * 处理删除交易请求
     */
    @GetMapping("/delete/{id}")
    public String deleteTransaction(@PathVariable String id,
                                  RedirectAttributes redirectAttributes) {
        // 删除交易记录
        transactionService.deleteTransaction(id);
        
        // 添加成功消息
        redirectAttributes.addFlashAttribute("successMessage", "交易记录删除成功");
        
        return "redirect:/transactions";
    }
} 