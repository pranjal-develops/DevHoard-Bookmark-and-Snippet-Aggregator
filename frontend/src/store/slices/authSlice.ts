import { createSlice, type PayloadAction } from "@reduxjs/toolkit"; // Core Redux Toolkit primitives for slice derivation

/**
 * State interface for the authentication domain.
 * Manages identity tokens, user metadata, and session lifecycle status.
 */
interface AuthState {
    user: string | null; // Authenticated principal's username
    token: string | null; // Stateless JWT used for request authorization
    isAuthenticated: boolean; // Computed flag for quick authentication checks
    loading: boolean; // Pending state for asynchronous authentication tasks
    error: string | null; // Field for capturing authentication failure details
}

/**
 * Initial state configuration with rehydration logic.
 * Attempts to restore the session from localStorage to prevent state loss on browser refresh.
 */
const initialState: AuthState = {
    user: localStorage.getItem('user'),
    token: localStorage.getItem('token'),
    isAuthenticated: !!localStorage.getItem('token'), // Boolean coercion based on token existence
    loading: false,
    error: null,
}

/**
 * Redux slice for authentication management.
 * Handles the synchronous state updates required for login and logout workflows.
 */
export const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        /**
         * Persists the authentication context following a successful credential verification.
         * Updates both the application state and the browser's persistent storage.
         */
        setAuth: (state, action: PayloadAction<{ user: string, token: string }>) => {
            state.user = action.payload.user;
            state.token = action.payload.token;
            state.isAuthenticated = true;
            
            // Persistence: Syncing new credentials to the browser storage
            localStorage.setItem('user', action.payload.user);
            localStorage.setItem('token', action.payload.token);
        },

        /**
         * Terminates the current session by performing an atomic cleanup of identity state.
         */
        logout: (state) => {
            state.user = null;
            state.token = null;
            state.isAuthenticated = false;
            
            // Cleanup: Removing credentials from persistent storage to prevent unauthorized access
            localStorage.removeItem('user');
            localStorage.removeItem('token');
        }
    }
})

// Exporting actions for use in functional components and interceptors
export const { setAuth, logout } = authSlice.actions;

// Exporting the reducer for store registration
export default authSlice.reducer;

