import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

interface UIState {
    isDark: boolean;
    isSidebarOpen: boolean;
    showToast: boolean;
}

const initialState: UIState = {
    isDark: true,
    isSidebarOpen: typeof window !== 'undefined' && window.matchMedia('(max-width:600px)').matches ? false : true,
    showToast: false,
};

export const uiSlice = createSlice({
    name: 'ui',
    initialState,
    reducers: {
        toggleTheme: (state) => { state.isDark = !state.isDark; },
        toggleSidebar: (state) => { state.isSidebarOpen = !state.isSidebarOpen; },
        setShowToast: (state, action: PayloadAction<boolean>) => { state.showToast = action.payload; },
    },
});

export const { toggleTheme, toggleSidebar, setShowToast } = uiSlice.actions;
export default uiSlice.reducer;
