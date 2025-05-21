// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 绑定表单提交事件
    document.getElementById('transactionForm').addEventListener('submit', handleFormSubmit);
    
    // 获取当前服务器时间并设置为默认交易时间
    setCurrentServerTime();
    
    // 检查URL参数是否有ID
    const urlParams = new URLSearchParams(window.location.search);
    const transactionId = urlParams.get('id');
    
    if (transactionId) {
        // 编辑模式
        document.getElementById('pageTitle').textContent = '编辑交易';
        loadTransaction(transactionId);
    } else {
        // 创建模式
        document.getElementById('pageTitle').textContent = '创建交易';
    }
});

// 设置当前服务器时间
function setCurrentServerTime() {
    fetch('/api/transactions/current-time')
        .then(response => response.json())
        .then(data => {
            if (data.success && data.data) {
                // 服务器返回的时间格式为ISO格式，需要处理成HTML datetime-local需要的格式
                const serverTime = data.data;
                // 只保留到分钟
                const formattedTime = serverTime.substring(0, 16);
                
                document.getElementById('transactionTime').value = formattedTime;
            } else {
                // 如果API调用失败，使用浏览器本地时间
                setLocalTime();
            }
        })
        .catch(error => {
            console.error('获取服务器时间失败:', error);
            // 如果API调用失败，使用浏览器本地时间
            setLocalTime();
        });
}

// 使用浏览器本地时间
function setLocalTime() {
    const transactionTimeField = document.getElementById('transactionTime');
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const formattedDate = `${year}-${month}-${day}T${hours}:${minutes}`;
    transactionTimeField.value = formattedDate;
}

// 加载交易数据
function loadTransaction(id) {
    fetch(`/api/transactions/${id}`)
        .then(response => response.json())
        .then(data => {
            if (data.success && data.data) {
                fillFormWithTransaction(data.data);
            } else {
                showAlert(data.message || '获取交易记录失败', 'danger');
            }
        })
        .catch(error => {
            console.error('获取交易记录失败:', error);
            showAlert('获取交易记录失败，请稍后重试', 'danger');
        });
}

// 填充表单数据
function fillFormWithTransaction(transaction) {
    document.getElementById('id').value = transaction.id;
    document.getElementById('type').value = transaction.type;
    document.getElementById('amount').value = transaction.amount;
    document.getElementById('status').value = transaction.status;
    document.getElementById('description').value = transaction.description || '';
    
    // 格式化交易时间
    if (transaction.transactionTime) {
        const transactionTime = new Date(transaction.transactionTime);
        const year = transactionTime.getFullYear();
        const month = String(transactionTime.getMonth() + 1).padStart(2, '0');
        const day = String(transactionTime.getDate()).padStart(2, '0');
        const hours = String(transactionTime.getHours()).padStart(2, '0');
        const minutes = String(transactionTime.getMinutes()).padStart(2, '0');
        const formattedDate = `${year}-${month}-${day}T${hours}:${minutes}`;
        document.getElementById('transactionTime').value = formattedDate;
    }
}

// 处理表单提交
function handleFormSubmit(event) {
    event.preventDefault();
    
    // 获取表单数据
    const id = document.getElementById('id').value;
    const type = document.getElementById('type').value;
    const amount = document.getElementById('amount').value;
    const transactionTime = document.getElementById('transactionTime').value;
    const status = document.getElementById('status').value;
    const description = document.getElementById('description').value;
    
    // 表单验证
    if (!type || !amount || !transactionTime || !status) {
        showAlert('请填写所有必填字段', 'danger');
        return;
    }
    
    // 构建提交数据
    const transaction = {
        id: id || null,
        type: type,
        amount: parseFloat(amount),
        transactionTime: new Date(transactionTime).toISOString(),
        status: status,
        description: description
    };
    
    // 判断是创建还是编辑
    const isCreate = !id;
    const url = isCreate ? '/api/transactions' : `/api/transactions/${id}`;
    const method = isCreate ? 'POST' : 'PUT';
    
    // 发送请求
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(transaction)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // 跳转到列表页面并显示成功消息
            window.location.href = `/transactions.html?successMessage=${isCreate ? '交易记录创建成功' : '交易记录更新成功'}`;
        } else {
            showAlert(data.message || (isCreate ? '创建交易记录失败' : '更新交易记录失败'), 'danger');
        }
    })
    .catch(error => {
        console.error(isCreate ? '创建交易记录失败' : '更新交易记录失败', error);
        showAlert(isCreate ? '创建交易记录失败，请稍后重试' : '更新交易记录失败，请稍后重试', 'danger');
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