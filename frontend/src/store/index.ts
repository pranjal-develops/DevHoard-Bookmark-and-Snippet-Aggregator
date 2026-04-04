import { configureStore } from '@reduxjs/toolkit';
import uiReducer from './slices/uiSlice';
import bookmarkReducer from './slices/bookmarksSlice';

export const store = configureStore({
    reducer: {
        ui: uiReducer,
        bookmarks: bookmarkReducer
    },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
