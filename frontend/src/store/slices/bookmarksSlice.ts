import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

interface BookmarkState {
    items: any[];
    selectedCategory: string | null;
    favoritesOnly: boolean;
    searchText: string;
    updateSignal: boolean;
}

const initialState: BookmarkState = {
    items: [],
    selectedCategory: null,
    favoritesOnly: false,
    searchText: "",
    updateSignal: false
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
        },
        setSearchText: (state, action: PayloadAction<string>) => {
            state.searchText = action.payload;
        },
        triggerRefresh: (state) => {
            state.updateSignal = !state.updateSignal;
        }
    }
})

export const { setBookmarks, setSelectedCategory, setFavoritesOnly, setSearchText, triggerRefresh } = BookmarkState.actions;
export default BookmarkState.reducer;  