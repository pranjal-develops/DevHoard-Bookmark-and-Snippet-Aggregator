import { createSlice, type PayloadAction } from '@reduxjs/toolkit'; // Redux Toolkit for slice-based state management

/**
 * State definition for the User Interface layer.
 * Manages global visibility states, theme preferences, and modal orchestration.
 */
interface UIState {
    isDark: boolean; // Flag for dark mode thematic preference
    isSidebarOpen: boolean; // Visibility status of the navigation sidebar
    showToast: boolean; // Trigger for the global floating notification system
    isAuthOpen: boolean; // Visibility status of the authentication modal
    authMode: 'login' | 'register'; // Active view within the authentication modal
}

/**
 * Initial state configuration with responsive defaults.
 * Automatically collapses the sidebar on mobile-range viewports.
 */
const initialState: UIState = {
    isDark: true,
    // Responsive Initialization: Defaulting to hidden sidebar on small screens (<600px)
    isSidebarOpen: typeof window !== 'undefined' && window.matchMedia('(max-width:600px)').matches ? false : true,
    showToast: false,
    isAuthOpen: false,
    authMode: 'login',
};

/**
 * Redux slice for UI state management.
 * Provides atomic reducers for toggling visual components and managing modal transitions.
 */
export const uiSlice = createSlice({
    name: 'ui',
    initialState,
    reducers: {
        /** Toggles the global theme between light and dark modes. */
        toggleTheme: (state) => { state.isDark = !state.isDark; },
        
        /** Toggles the visibility of the primary navigation sidebar. */
        toggleSidebar: (state) => { state.isSidebarOpen = !state.isSidebarOpen; },
        
        /** Controls the display lifecycle of the global toast notification. */
        setShowToast: (state, action: PayloadAction<boolean>) => { state.showToast = action.payload; },
        
        /** 
         * Opens the authentication modal in a specific mode (Login or Register).
         */
        openAuth: (state, action: PayloadAction<'login' | 'register'>) => {
            state.authMode = action.payload;
            state.isAuthOpen = true;
        },
        
        /** Closes the authentication modal and resets its visibility. */
        closeAuth: (state) => { state.isAuthOpen = false; }
    },
});

// Exporting actions for dispatching from UI components
export const { toggleTheme, toggleSidebar, setShowToast, openAuth, closeAuth } = uiSlice.actions;

// Exporting the reducer for central store integration
export default uiSlice.reducer;

