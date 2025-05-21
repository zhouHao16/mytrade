package com.example.banktransaction.config;

import com.example.banktransaction.model.Transaction;
import com.example.banktransaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 数据初始化组件
 * 在应用启动时初始化测试数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TransactionService transactionService;
    private final Random random = new Random();

    // 交易类型列表
    private final List<String> transactionTypes = Arrays.asList("存款", "取款", "转账", "缴费", "退款");
    
    // 交易状态列表
    private final List<String> transactionStatuses = Arrays.asList("成功", "失败", "处理中", "待审核");
    
    // 交易描述模板列表
    private final List<String> descriptionTemplates = Arrays.asList(
            "来自%s的%s交易",
            "%s账户%s操作",
            "通过%s渠道%s",
            "%s%s交易",
            "客户%s的%s业务"
    );
    
    // 渠道列表
    private final List<String> channels = Arrays.asList("ATM", "网银", "手机银行", "柜台", "微信", "支付宝");
    
    // 账户名列表
    private final List<String> accounts = Arrays.asList("王明", "李红", "张伟", "刘芳", "陈晓", "赵丽", "杨勇");

    @Override
    public void run(String... args) {
        // 检查是否已有数据
        if (transactionService.queryTransactions(com.example.banktransaction.model.TransactionQuery.builder().build()).getTotalElements() > 0) {
            log.info("已有交易数据，跳过初始化");
            return;
        }

        log.info("开始初始化20条交易数据...");
        
        // 创建20条随机交易记录
        for (int i = 0; i < 20; i++) {
            Transaction transaction = createRandomTransaction();
            transactionService.createTransaction(transaction);
            log.info("创建交易记录: {}", transaction.getId());
        }
        
        log.info("交易数据初始化完成");
    }
    
    /**
     * 创建随机交易记录
     */
    private Transaction createRandomTransaction() {
        String type = randomItem(transactionTypes);
        
        // 根据交易类型设置金额范围
        BigDecimal amount;
        if ("存款".equals(type) || "转账".equals(type)) {
            amount = randomAmount(100, 10000);
        } else if ("取款".equals(type)) {
            amount = randomAmount(50, 5000);
        } else if ("缴费".equals(type)) {
            amount = randomAmount(10, 1000);
        } else {
            amount = randomAmount(20, 2000);
        }
        
        // 生成随机时间（最近30天内）
        LocalDateTime transactionTime = randomDateTime(30);
        
        // 生成随机描述
        String description = generateDescription(type);
        
        // 根据交易类型设置状态概率
        String status;
        int statusRandom = random.nextInt(100);
        if (statusRandom < 80) {
            status = "成功";  // 80%的概率是成功
        } else if (statusRandom < 90) {
            status = "失败";  // 10%的概率是失败
        } else if (statusRandom < 95) {
            status = "处理中";  // 5%的概率是处理中
        } else {
            status = "待审核";  // 5%的概率是待审核
        }
        
        return Transaction.builder()
                .type(type)
                .amount(amount)
                .description(description)
                .transactionTime(transactionTime)
                .status(status)
                .build();
    }
    
    /**
     * 从列表中随机选择一项
     */
    private <T> T randomItem(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }
    
    /**
     * 生成随机金额
     */
    private BigDecimal randomAmount(int min, int max) {
        // 生成两位小数的随机金额
        double amount = min + (max - min) * random.nextDouble();
        return BigDecimal.valueOf(Math.round(amount * 100) / 100.0);
    }
    
    /**
     * 生成随机日期时间（最近n天内）
     */
    private LocalDateTime randomDateTime(int daysBack) {
        long minDay = LocalDateTime.now().minusDays(daysBack).toLocalDate().toEpochDay();
        long maxDay = LocalDateTime.now().toLocalDate().toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay + 1);
        
        LocalDateTime randomDate = LocalDateTime.now()
                .withHour(random.nextInt(24))
                .withMinute(random.nextInt(60))
                .withSecond(random.nextInt(60));
                
        if (randomDay < maxDay) {
            randomDate = randomDate.toLocalDate().atTime(
                    random.nextInt(24),
                    random.nextInt(60),
                    random.nextInt(60)
            );
        }
        
        return randomDate;
    }
    
    /**
     * 生成交易描述
     */
    private String generateDescription(String type) {
        String template = randomItem(descriptionTemplates);
        String channel = randomItem(channels);
        String account = randomItem(accounts);
        
        if (template.contains("%s的%s")) {
            return String.format(template, account, type);
        } else if (template.contains("账户%s")) {
            return String.format(template, account, type);
        } else if (template.contains("渠道%s")) {
            return String.format(template, channel, type);
        } else if (template.contains("%s%s")) {
            return String.format(template, channel, type);
        } else {
            return String.format(template, account, type);
        }
    }
} 