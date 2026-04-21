import axios from 'axios';
import { store } from '../store';
import { logout } from '../store/slices/authSlice';

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
});

api.interceptors.response.use(
    (response) => {
        // Handle custom session expiry signal from backend
        const isExpired = response.headers['x-session-expiry'] === 'true';
        if (isExpired) {
            store.dispatch(logout());
        }
        return response;
    },
    (error) => {
        // Auto-logout on unauthorized requests
        if (error.response && error.response.status === 401) {
            store.dispatch(logout());
        }
        return Promise.reject(error);
    }
);

export default api;

