const messageBox = document.getElementById('message');

function showMessage(text, type = 'error') {
  if (!messageBox) return;
  messageBox.textContent = text;
  messageBox.className = `message ${type}`;
}

async function parseResponse(response) {
  const text = await response.text();
  try { return text ? JSON.parse(text) : {}; }
  catch { return { message: text || `HTTP ${response.status}` }; }
}

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;').replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;').replaceAll('"', '&quot;').replaceAll("'", '&#039;');
}

// ==================== LOGIN ====================
const loginForm = document.getElementById('loginForm');
if (loginForm) {
  loginForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    showMessage('Đang đăng nhập...', 'success');
    const payload = {
      email: document.getElementById('email').value,
      password: document.getElementById('password').value
    };

    try {
      const response = await fetch('/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const data = await parseResponse(response);
      if (!response.ok) throw new Error(data.message || 'Đăng nhập thất bại');

      // Giống slide: lưu JWT rồi gửi lại trong Authorization: Bearer <token>.
      localStorage.setItem('jwt_token', data.token);
      localStorage.setItem('jwt_user', JSON.stringify(data.user || {}));
      window.location.href = '/user/profile';
    } catch (error) {
      showMessage(error.message || 'Không thể đăng nhập');
    }
  });
}

// ==================== REGISTER ====================
const registerForm = document.getElementById('registerForm');
if (registerForm) {
  registerForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    showMessage('Đang tạo tài khoản...', 'success');
    const payload = {
      fullName: document.getElementById('fullName').value,
      email: document.getElementById('email').value,
      password: document.getElementById('password').value
    };

    try {
      const response = await fetch('/auth/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const data = await parseResponse(response);
      if (!response.ok) throw new Error(data.message || 'Đăng ký thất bại');
      showMessage('Đăng ký thành công. Đang chuyển sang trang đăng nhập...', 'success');
      setTimeout(() => window.location.href = '/login', 700);
    } catch (error) {
      showMessage(error.message || 'Không thể đăng ký');
    }
  });
}

// ==================== PROFILE / PROTECTED API ====================
const profileBox = document.getElementById('profile');
if (profileBox) {
  const token = localStorage.getItem('jwt_token');
  const profileMessage = document.getElementById('profileMessage');
  const usersBox = document.getElementById('users');

  if (!token) window.location.replace('/login');

  function authHeaders() {
    return { 'Authorization': `Bearer ${token}` };
  }

  function info(label, value) {
    return `<div class="info"><span>${escapeHtml(label)}</span><strong>${escapeHtml(value)}</strong></div>`;
  }

  async function loadProfile() {
    try {
      const response = await fetch('/users/me', { headers: authHeaders() });
      const data = await parseResponse(response);
      if (!response.ok) throw new Error(data.message || 'Không thể tải profile');

      const imageName = data.images || 'u1.jpg';
      profileBox.innerHTML = `<div class="avatar-box"><img class="avatar" src="/images/${encodeURIComponent(imageName)}" alt="Avatar"></div>` + [
        info('ID', data.id),
        info('Họ tên', data.fullName),
        info('Email', data.email),
        info('Images', imageName),
        info('Quyền', data.role)
      ].join('');
      profileMessage.textContent = 'JWT hợp lệ - xác thực thành công bằng Nimbus.';
      profileMessage.className = 'message success';
    } catch (error) {
      profileMessage.textContent = error.message;
      profileMessage.className = 'message error';
      localStorage.removeItem('jwt_token');
    }
  }

  async function loadUsers() {
    usersBox.innerHTML = '<p>Đang tải...</p>';
    try {
      const response = await fetch('/users', { headers: authHeaders() });
      const data = await parseResponse(response);
      if (!response.ok) throw new Error(data.message || 'Không thể tải danh sách');

      const rows = data.map(user => `<tr>
        <td>${escapeHtml(user.id)}</td>
        <td>${escapeHtml(user.fullName)}</td>
        <td>${escapeHtml(user.email)}</td>
        <td>${escapeHtml(user.images)}</td>
        <td>${escapeHtml(user.role)}</td>
      </tr>`).join('');
      usersBox.innerHTML = `<table><thead><tr><th>ID</th><th>Họ tên</th><th>Email</th><th>Images</th><th>Role</th></tr></thead><tbody>${rows}</tbody></table>`;
    } catch (error) {
      usersBox.innerHTML = `<p class="message error">${escapeHtml(error.message)}</p>`;
    }
  }

  document.getElementById('loadUsers').addEventListener('click', loadUsers);
  document.getElementById('logout').addEventListener('click', () => {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('jwt_user');
    window.location.href = '/login';
  });

  loadProfile();
}
