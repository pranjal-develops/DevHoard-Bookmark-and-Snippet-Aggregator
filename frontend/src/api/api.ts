import axios from 'axios';
import { store } from '../store';
import { logout } from '../store/slices/authSlice';

// Create the centralized API instance
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
});

// THE WATCHER: Listen to every single response from the server
api.interceptors.response.use(
    (response) => {
        // Check for our custom "Death Signal" header
        // (Note: Axios converts header names to lowercase!)
        const isExpired = response.headers['x-session-expiry'] === 'true';

        if (isExpired) {
            console.warn('[Security Watcher] Session expired! Triggering auto-logout...');
            store.dispatch(logout());
        }
        return response;
    },
    (error) => {
        // Fallback: If we get a 401, also trigger logout
        if (error.response && error.response.status === 401) {
            store.dispatch(logout());
        }
        return Promise.reject(error);
    }
);

export default api;
