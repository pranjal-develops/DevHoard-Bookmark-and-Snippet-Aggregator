import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

interface UIState {
    isDark: boolean;
    isSidebarOpen: boolean;
    showToast: boolean;
    isAuthOpen: boolean;
    authMode: 'login' | 'register';
}

const initialState: UIState = {
    isDark: true,
    // Collapse sidebar by default on small screens
    isSidebarOpen: typeof window !== 'undefined' && window.matchMedia('(max-width:600px)').matches ? false : true,
    showToast: false,
    isAuthOpen: false,
    authMode: 'login',
};

export const uiSlice = createSlice({
    name: 'ui',
    initialState,
    reducers: {
        toggleTheme: (state) => { state.isDark = !state.isDark; },
        toggleSidebar: (state) => { state.isSidebarOpen = !state.isSidebarOpen; },
        setShowToast: (state, action: PayloadAction<boolean>) => { state.showToast = action.payload; },
        openAuth: (state, action: PayloadAction<'login' | 'register'>) => {
            state.authMode = action.payload;
            state.isAuthOpen = true;
        },
        closeAuth: (state) => { state.isAuthOpen = false; }
    },
});

export const { toggleTheme, toggleSidebar, setShowToast, openAuth, closeAuth } = uiSlice.actions;
export default uiSlice.reducer;


