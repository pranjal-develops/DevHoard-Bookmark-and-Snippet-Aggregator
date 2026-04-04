import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

interface BookmarkState {
    items: any[];
    selectedCategory: string | null;
    favoritesOnly: boolean;
}

const initialState: BookmarkState = {
    items: [],
    selectedCategory: null,
    favoritesOnly: false
}

export const BookmarkState = createSlice({
    name: "bookmarks",
    initialState,
    reducers: {
        setBookmarks: (state, action: PayloadAction<any[]>) => {
            state.items = action.payload;
        },
        setSelectedCategory: (state, action: PayloadAction<string | null>) => {
            state.selectedCategory = action.payload;
        },
        setFavoritesOnly: (state, action: PayloadAction<boolean>) => {
            state.favoritesOnly = action.payload;
        }
    }
})

export const { setBookmarks, setSelectedCategory, setFavoritesOnly } = BookmarkState.actions;
export default BookmarkState.reducer;  