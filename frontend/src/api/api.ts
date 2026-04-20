import axios from 'axios'; // Core HTTP client for API interactions
import { store } from '../store'; // Application state store for cross-component dispatch
import { logout } from '../store/slices/authSlice'; // Authentication action for session termination

/**
 * Centralized Axios instance configuration.
 * Encapsulates the base URL resolution and interceptor logic for global request/response handling.
 */
const api = axios.create({
    // Resolution: Prioritize environment variables for baseURL, with a local fallback for development
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
});

/**
 * Response Interceptor: Lifecycle monitoring for all incoming server responses.
 * Primarily handles session expiry detection and automated state cleanup.
 */
api.interceptors.response.use(
    (response) => {
        /**
         * Session Validation:
         * Interrogating the response headers for the custom 'x-session-expiry' signal.
         * Note: Axios automatically normalizes header keys to lowercase for consistent access.
         */
        const isExpired = response.headers['x-session-expiry'] === 'true';

        if (isExpired) {
            // Log security event and trigger state synchronization (logout)
            console.warn('[Security Watcher] Remote session invalidation detected. Proceeding with automated logout.');
            store.dispatch(logout());
        }
        return response;
    },
    (error) => {
        /**
         * Error Handling:
         * If the server returns a 401 Unauthorized status, we assume the local JWT is invalid or missing.
         * Triggering a logout ensures the UI returns to a consistent unauthenticated state.
         */
        if (error.response && error.response.status === 401) {
            console.error('[Security Watcher] Unauthorized request detected. Forcing session termination.');
            store.dispatch(logout());
        }
        
        // Propagation: Re-throwing the error for local component-level handling
        return Promise.reject(error);
    }
);

export default api;

