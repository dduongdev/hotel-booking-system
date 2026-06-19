/**
 * LuxeStay - API Client
 * Handles JWT authentication and API communication
 */

const API_BASE = '/api/v1';

// ===== Token Management =====
const TokenManager = {
    getAccessToken() {
        return localStorage.getItem('accessToken');
    },
    getRefreshToken() {
        return localStorage.getItem('refreshToken');
    },
    setTokens(accessToken, refreshToken) {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
    },
    clearTokens() {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        localStorage.removeItem('user');
    },
    saveUser(user) {
        localStorage.setItem('user', JSON.stringify(user));
    },
    getUser() {
        try {
            return JSON.parse(localStorage.getItem('user'));
        } catch {
            return null;
        }
    },
    isAuthenticated() {
        return !!this.getAccessToken();
    }
};

// ===== Toast Manager (LuxeStay Style) =====
const ToastManager = {
    show(message, type = 'info') {
        const container = document.getElementById('ls-toast-container');
        if (!container) {
            const div = document.createElement('div');
            div.id = 'ls-toast-container';
            div.className = 'ls-toast-container';
            document.body.appendChild(div);
        }
        
        const toast = document.createElement('div');
        toast.className = 'ls-toast';
        
        const iconMap = {
            success: 'check_circle',
            error: 'error',
            warning: 'warning',
            info: 'info_outline'
        };
        
        toast.innerHTML = `
            <span class="ls-icon ls-toast__icon">${iconMap[type] || iconMap.info}</span>
            <p class="ls-toast__text">${message}</p>
        `;
        
        document.getElementById('ls-toast-container').appendChild(toast);
        
        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transition = 'opacity 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
};

// ===== HTTP Client =====
async function apiRequest(endpoint, options = {}) {
    const { method = 'GET', body, useAuth = true, params } = options;
    
    const headers = {
        'Content-Type': 'application/json',
    };
    
    if (useAuth && TokenManager.getAccessToken()) {
        headers['Authorization'] = `Bearer ${TokenManager.getAccessToken()}`;
    }
    
    let url = `${API_BASE}${endpoint}`;
    if (params) {
        const searchParams = new URLSearchParams();
        Object.entries(params).forEach(([key, value]) => {
            if (value !== undefined && value !== null && value !== '') {
                searchParams.append(key, value);
            }
        });
        const qs = searchParams.toString();
        if (qs) url += `?${qs}`;
    }
    
    const config = {
        method,
        headers,
    };
    
    if (body) {
        config.body = JSON.stringify(body);
    }
    
    try {
        const response = await fetch(url, config);
        
        // Handle 401 - try refresh token
        if (response.status === 401 && useAuth && TokenManager.getRefreshToken()) {
            const refreshed = await tryRefreshToken();
            if (refreshed) {
                headers['Authorization'] = `Bearer ${TokenManager.getAccessToken()}`;
                config.headers = headers;
                const retryResponse = await fetch(url, config);
                return handleResponse(retryResponse);
            } else {
                TokenManager.clearTokens();
                window.location.href = '/login';
                throw new Error('Session expired. Please login again.');
            }
        }
        
        return handleResponse(response);
    } catch (error) {
        if (error.name === 'TypeError' && error.message === 'Failed to fetch') {
            throw new Error('Network error. Please check your connection.');
        }
        throw error;
    }
}

async function handleResponse(response) {
    const contentType = response.headers.get('content-type');
    let data;
    
    if (contentType && contentType.includes('application/json')) {
        data = await response.json();
    } else {
        data = await response.text();
    }
    
    if (!response.ok) {
        const errorMessage = data?.message || `Request failed with status ${response.status}`;
        const error = new Error(errorMessage);
        error.status = response.status;
        error.data = data;
        throw error;
    }
    
    // Auto-unwrap ApiResponse wrapper if present
    if (data && typeof data === 'object' && 'success' in data && 'data' in data) {
        return data.data;
    }
    return data;
}

async function tryRefreshToken() {
    try {
        const response = await fetch(`${API_BASE}/auth/refresh-token`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ refreshToken: TokenManager.getRefreshToken() })
        });
        
        if (response.ok) {
            const raw = await response.json();
            const data = (raw && raw.data) ? raw.data : raw;
            TokenManager.setTokens(data.accessToken, data.refreshToken);
            return true;
        }
        return false;
    } catch {
        return false;
    }
}

// ===== API Methods =====

// Auth API
const AuthAPI = {
    async login(username, password) {
        const data = await apiRequest('/auth/login', {
            method: 'POST',
            body: { username, password },
            useAuth: false
        });
        TokenManager.setTokens(data.accessToken, data.refreshToken);
        return data;
    },
    
    async register(username, password, phoneNumber) {
        const data = await apiRequest('/users/register', {
            method: 'POST',
            body: { username, password, phoneNumber },
            useAuth: false
        });
        if (data && data.accessToken && data.refreshToken) {
            TokenManager.setTokens(data.accessToken, data.refreshToken);
        }
        return data;
    },
    
    async sendOtpForActivation() {
        return apiRequest('/otp/send-for-activation', {
            method: 'POST'
        });
    },
    
    async activateByOtp(otpCode) {
        const data = await apiRequest('/users/verify-phone', {
            method: 'POST',
            body: { otpCode }
        });
        if (data && data.accessToken && data.refreshToken) {
            TokenManager.setTokens(data.accessToken, data.refreshToken);
        }
        return data;
    },
    
    logout() {
        TokenManager.clearTokens();
        window.location.href = '/login';
    },

    async changePassword(currentPassword, newPassword, confirmNewPassword) {
        return apiRequest('/auth/change-password', {
            method: 'POST',
            body: { currentPassword, newPassword, confirmNewPassword }
        });
    }
};

// Room Type API
const RoomTypeAPI = {
    async getAll(page = 0, size = 1000) {
        return apiRequest('/room-types', {
            useAuth: false,
            params: { page, size }
        });
    },
    
    async getAvailability(checkIn, checkOut) {
        return apiRequest('/room-types/availability', {
            useAuth: false,
            params: { checkIn, checkOut }
        });
    },
    
    async create(data) {
        return apiRequest('/room-types', {
            method: 'POST',
            body: data
        });
    },
    
    async update(id, data) {
        return apiRequest(`/room-types/${id}`, {
            method: 'PUT',
            body: data
        });
    },
    
    async changeHiddenState(id, hidden) {
        return apiRequest(`/room-types/${id}/hidden`, {
            method: 'PATCH',
            body: { hidden }
        });
    },

    async createWithImage(data, imageFile) {
        const formData = new FormData();
        formData.append('name', data.name);
        if (data.description) formData.append('description', data.description);
        formData.append('capacity', data.capacity);
        formData.append('pricePerNight', data.pricePerNight);
        if (imageFile) formData.append('image', imageFile);

        const headers = {};
        if (TokenManager.getAccessToken()) {
            headers['Authorization'] = `Bearer ${TokenManager.getAccessToken()}`;
        }

        const response = await fetch(`${API_BASE}/room-types`, {
            method: 'POST',
            headers,
            body: formData
        });
        return handleResponse(response);
    },

    async updateWithImage(id, data, imageFile) {
        const formData = new FormData();
        formData.append('name', data.name);
        if (data.description) formData.append('description', data.description);
        formData.append('capacity', data.capacity);
        formData.append('pricePerNight', data.pricePerNight);
        if (imageFile) formData.append('image', imageFile);

        const headers = {};
        if (TokenManager.getAccessToken()) {
            headers['Authorization'] = `Bearer ${TokenManager.getAccessToken()}`;
        }

        const response = await fetch(`${API_BASE}/room-types/${id}`, {
            method: 'PUT',
            headers,
            body: formData
        });
        return handleResponse(response);
    }
};

// Room API
const RoomAPI = {
    async getAll(page = 0, size = 10) {
        return apiRequest('/rooms', {
            useAuth: false,
            params: { page, size }
        });
    },
    
    async getAvailable(checkIn, checkOut, page = 0, size = 10) {
        return apiRequest('/rooms/available', {
            useAuth: false,
            params: { checkIn, checkOut, page, size }
        });
    },
    
    async create(data) {
        return apiRequest('/rooms', {
            method: 'POST',
            body: data
        });
    },
    
    async update(id, data) {
        return apiRequest(`/rooms/${id}`, {
            method: 'PUT',
            body: data
        });
    }
};

// Booking API
const BookingAPI = {
    async make(data) {
        return apiRequest('/bookings', {
            method: 'POST',
            body: data
        });
    },
    
    async getMyBookings(page = 0, size = 10) {
        return apiRequest('/bookings/me', {
            params: { page, size }
        });
    },
    
    async cancelMyBooking(bookingId) {
        return apiRequest('/bookings/me/cancel', {
            method: 'DELETE',
            body: { bookingId }
        });
    },
    
    async getAll(page = 0, size = 10) {
        return apiRequest('/bookings', {
            params: { page, size }
        });
    },
    
    async cancelByManager(id) {
        return apiRequest(`/bookings/${id}/cancel`, {
            method: 'PATCH'
        });
    },

    async getBestFitRooms(bookingId) {
        return apiRequest(`/bookings/${bookingId}/best-fit-rooms`);
    },

    async checkIn(bookingId, roomId) {
        return apiRequest(`/bookings/${bookingId}/check-in`, {
            method: 'PATCH',
            body: { roomId }
        });
    },

    async checkOut(bookingId) {
        return apiRequest(`/bookings/${bookingId}/check-out`, {
            method: 'PATCH'
        });
    }
};

// ===== JWT Decoder (Base64URL-safe) =====
function decodeJwt(token) {
    try {
        const parts = token.split('.');
        if (parts.length !== 3) {
            console.error('Invalid JWT: expected 3 parts, got', parts.length);
            return null;
        }
        // Base64URL -> Base64: replace - with +, _ with /, add padding
        let base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
        while (base64.length % 4 !== 0) {
            base64 += '=';
        }
        const decoded = atob(base64);
        return JSON.parse(decoded);
    } catch (e) {
        console.error('Failed to decode JWT:', e);
        return null;
    }
}

function extractRoleFromPayload(payload) {
    if (!payload) return 'CUSTOMER';
    // Try direct role field first
    if (payload.role) {
        return typeof payload.role === 'string' ? payload.role.replace('ROLE_', '') : 'CUSTOMER';
    }
    // Extract from authorities array
    const auth = payload.authorities && payload.authorities[0];
    if (!auth) return 'CUSTOMER';
    if (typeof auth === 'string') {
        return auth.replace('ROLE_', '');
    }
    if (auth.authority) {
        return auth.authority.replace('ROLE_', '');
    }
    return 'CUSTOMER';
}

// ===== Utility Functions =====

function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

function formatDateTime(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Legacy showToast - routes to ToastManager
function showToast(message, type = 'info') {
    ToastManager.show(message, type);
}

function getStatusBadgeClass(status) {
    switch (status) {
        case 'CONFIRMED': return 'ls-badge ls-badge--confirmed';
        case 'CHECKED_IN': return 'ls-badge ls-badge--checked-in';
        case 'CHECKED_OUT': return 'ls-badge ls-badge--secondary';
        case 'CANCELLED': return 'ls-badge ls-badge--cancelled';
        default: return 'ls-badge ls-badge--info';
    }
}

function getStatusText(status) {
    switch (status) {
        case 'CONFIRMED': return 'Đã xác nhận';
        case 'CHECKED_IN': return 'Đã nhận phòng';
        case 'CHECKED_OUT': return 'Đã trả phòng';
        case 'CANCELLED': return 'Đã hủy';
        default: return status || 'Không xác định';
    }
}

function getRoleBadgeClass(role) {
    return role === 'MANAGER' ? 'ls-badge ls-badge--warning' : 'ls-badge ls-badge--info';
}

function isManager() {
    const user = TokenManager.getUser();
    return user && user.role === 'MANAGER';
}

function isCustomer() {
    const user = TokenManager.getUser();
    return user && user.role === 'CUSTOMER';
}

function escapeHtml(str) {
    if (typeof str !== 'string') return String(str || '');
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}
