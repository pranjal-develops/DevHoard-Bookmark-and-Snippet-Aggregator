import { configureStore } from '@reduxjs/toolkit';
import uiReducer from './slices/uiSlice';
import bookmarkReducer from './slices/bookmarksSlice';
import authReducer from './slices/authSlice';

export const store = configureStore({
    reducer: {
        ui: uiReducer,
        bookmarks: bookmarkReducer,
        auth: authReducer
    },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
