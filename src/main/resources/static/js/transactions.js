// 全局变量
let currentPage = 0;
const pageSize = 10;

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 初始加载交易列表
    loadTransactions();
    
    // 绑定查询按钮点击事件
    document.getElementById('searchBtn').addEventListener('click', function() {
        currentPage = 0;
        loadTransactions();
    });
    
    // 绑定重置按钮点击事件
    document.getElementById('resetBtn').addEventListener('click', function() {
        document.getElementById('searchForm').reset();
        currentPage = 0;
        loadTransactions();
    });
    
    // 检查URL参数是否有成功消息
    const urlParams = new URLSearchParams(window.location.search);
    const successMessage = urlParams.get('successMessage');
    if (successMessage) {
        showAlert(successMessage, 'success');
        // 清除URL参数
        window.history.replaceState({}, document.title, '/transactions.html');
    }
});

// 加载交易列表
function loadTransactions() {
    // 构建查询参数
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;
    const type = document.getElementById('type').value;
    const status = document.getElementById('status').value;
    
    let url = `/api/transactions?page=${currentPage}&size=${pageSize}`;
    if (startTime) url += `&startTime=${startTime}`;
    if (endTime) url += `&endTime=${endTime}`;
    if (type) url += `&type=${encodeURIComponent(type)}`;
    if (status) url += `&status=${encodeURIComponent(status)}`;
    
    // 发起API请求
    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                renderTransactionTable(data.data);
                renderPagination(data.data);
            } else {
                showAlert(data.message || '获取交易记录失败', 'danger');
            }
        })
        .catch(error => {
            console.error('获取交易记录失败:', error);
            showAlert('获取交易记录失败，请稍后重试', 'danger');
        });
}

// 渲染交易表格
function renderTransactionTable(pageResult) {
    const tableBody = document.getElementById('transactionTableBody');
    tableBody.innerHTML = '';
    
    // 更新总记录数
    document.getElementById('totalRecords').textContent = `共 ${pageResult.totalElements} 条记录`;
    
    // 没有数据
    if (pageResult.content.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = '<td colspan="6" class="text-center">没有找到交易记录</td>';
        tableBody.appendChild(row);
        return;
    }
    
    // 渲染每一行数据
    pageResult.content.forEach(transaction => {
        const row = document.createElement('tr');
        
        // 格式化交易时间
        const transactionTime = new Date(transaction.transactionTime);
        const formattedTime = transactionTime.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
        
        // 确定状态样式
        let statusClass = 'bg-secondary';
        if (transaction.status === '成功') statusClass = 'bg-success';
        else if (transaction.status === '失败') statusClass = 'bg-danger';
        else if (transaction.status === '处理中') statusClass = 'bg-warning';
        
        row.innerHTML = `
            <td>${transaction.type}</td>
            <td>${transaction.amount.toFixed(2)}</td>
            <td>${transaction.description || '-'}</td>
            <td>${formattedTime}</td>
            <td><span class="badge ${statusClass}">${transaction.status}</span></td>
            <td class="text-end">
                <a href="/transaction-form.html?id=${transaction.id}" class="btn btn-sm btn-primary">
                    <i class="bi bi-pencil"></i> 编辑
                </a>
                <button class="btn btn-sm btn-danger ms-1" onclick="deleteTransaction('${transaction.id}')">
                    <i class="bi bi-trash"></i> 删除
                </button>
            </td>
        `;
        
        tableBody.appendChild(row);
    });
}

// 渲染分页
function renderPagination(pageResult) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';
    
    if (pageResult.totalPages === 0) return;
    
    // 首页
    const firstPageItem = document.createElement('li');
    firstPageItem.className = `page-item${pageResult.first ? ' disabled' : ''}`;
    firstPageItem.innerHTML = `
        <a class="page-link" href="javascript:void(0)" onclick="goToPage(0)">首页</a>
    `;
    pagination.appendChild(firstPageItem);
    
    // 上一页
    const prevPageItem = document.createElement('li');
    prevPageItem.className = `page-item${pageResult.first ? ' disabled' : ''}`;
    prevPageItem.innerHTML = `
        <a class="page-link" href="javascript:void(0)" onclick="goToPage(${pageResult.currentPage - 1})">上一页</a>
    `;
    pagination.appendChild(prevPageItem);
    
    // 页码
    for (let i = 0; i < pageResult.totalPages; i++) {
        // 只显示当前页前后2页
        if (i >= pageResult.currentPage - 2 && i <= pageResult.currentPage + 2) {
            const pageItem = document.createElement('li');
            pageItem.className = `page-item${pageResult.currentPage === i ? ' active' : ''}`;
            pageItem.innerHTML = `
                <a class="page-link" href="javascript:void(0)" onclick="goToPage(${i})">${i + 1}</a>
            `;
            pagination.appendChild(pageItem);
        }
    }
    
    // 下一页
    const nextPageItem = document.createElement('li');
    nextPageItem.className = `page-item${pageResult.last ? ' disabled' : ''}`;
    nextPageItem.innerHTML = `
        <a class="page-link" href="javascript:void(0)" onclick="goToPage(${pageResult.currentPage + 1})">下一页</a>
    `;
    pagination.appendChild(nextPageItem);
    
    // 末页
    const lastPageItem = document.createElement('li');
    lastPageItem.className = `page-item${pageResult.last ? ' disabled' : ''}`;
    lastPageItem.innerHTML = `
        <a class="page-link" href="javascript:void(0)" onclick="goToPage(${pageResult.totalPages - 1})">末页</a>
    `;
    pagination.appendChild(lastPageItem);
}

// 跳转到指定页
function goToPage(page) {
    currentPage = page;
    loadTransactions();
}

// 删除交易
function deleteTransaction(id) {
    if (!confirm('确定要删除这条交易记录吗？')) return;
    
    fetch(`/api/transactions/${id}`, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showAlert('交易记录删除成功', 'success');
            loadTransactions(); // 重新加载列表
        } else {
            showAlert(data.message || '删除交易记录失败', 'danger');
        }
    })
    .catch(error => {
        console.error('删除交易记录失败:', error);
        showAlert('删除交易记录失败，请稍后重试', 'danger');
    });
}

// 显示提示消息
function showAlert(message, type = 'info') {
    const alertContainer = document.getElementById('alertContainer');
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} alert-dismissible fade show`;
    alert.role = 'alert';
    alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    
    alertContainer.appendChild(alert);
    
    // 5秒后自动关闭
    setTimeout(() => {
        if (alert && alertContainer.contains(alert)) {
            alert.remove();
        }
    }, 5000);
} 