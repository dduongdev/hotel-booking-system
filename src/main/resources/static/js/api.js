/**
 * Hotel Booking System - API Client
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
            // Unwrap ApiResponse if present
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

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    if (!container) {
        const div = document.createElement('div');
        div.id = 'toast-container';
        div.className = 'toast-container';
        document.body.appendChild(div);
    }
    
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    
    document.getElementById('toast-container').appendChild(toast);
    
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function getStatusBadgeClass(status) {
    switch (status) {
        case 'CONFIRMED': return 'badge badge-success';
        case 'CHECKED_IN': return 'badge badge-info';
        case 'CHECKED_OUT': return 'badge badge-secondary';
        case 'CANCELLED': return 'badge badge-danger';
        default: return 'badge badge-info';
    }
}

function getStatusText(status) {
    switch (status) {
        case 'CONFIRMED': return 'Confirmed';
        case 'CHECKED_IN': return 'Checked In';
        case 'CHECKED_OUT': return 'Checked Out';
        case 'CANCELLED': return 'Cancelled';
        default: return status;
    }
}

function getRoleBadgeClass(role) {
    return role === 'MANAGER' ? 'badge badge-warning' : 'badge badge-info';
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
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}
