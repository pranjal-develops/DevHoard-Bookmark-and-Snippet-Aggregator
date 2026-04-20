import { createSlice, type PayloadAction } from "@reduxjs/toolkit"; // Library for efficient Redux state logic

/**
 * State definition for the Bookmark domain.
 * Manages the collection of entities alongside the active filter and search state.
 */
interface BookmarkState {
    items: any[]; // The primary collection of bookmark objects (Untyped here to accommodate partial schema flexibility)
    selectedCategory: string | null; // Currently active category filter
    favoritesOnly: boolean; // Toggle for restricting the view to favorite items
    searchText: string; // The active search query string
    updateSignal: boolean; // Reactive anchor used to force a logical 'refetch' in observation hooks
}

/**
 * Initial empty state configuration for the bookmarks slice.
 */
const initialState: BookmarkState = {
    items: [],
    selectedCategory: null,
    favoritesOnly: false,
    searchText: "",
    updateSignal: false
}

/**
 * Redux slice for bookmark management.
 * Provides the core state-mutation logic for search, filtering, and data hydration.
 */
export const BookmarkState = createSlice({
    name: "bookmarks",
    initialState,
    reducers: {
        /**
         * Hydrates the local bookmark collection with remote data.
         */
        setBookmarks: (state, action: PayloadAction<any[]>) => {
            state.items = action.payload;
        },

        /**
         * Sets the active category for filtering.
         */
        setSelectedCategory: (state, action: PayloadAction<string | null>) => {
            state.selectedCategory = action.payload;
        },

        /**
         * Toggles the favorites-only view mode.
         */
        setFavoritesOnly: (state, action: PayloadAction<boolean>) => {
            state.favoritesOnly = action.payload;
        },

        /**
         * Updates the global search string.
         */
        setSearchText: (state, action: PayloadAction<string>) => {
            state.searchText = action.payload;
        },

        /**
         * Toggles the updateSignal to notify observing hooks (e.g. useFetchData) 
         * that a manual data refresh is required.
         */
        triggerRefresh: (state) => {
            state.updateSignal = !state.updateSignal;
        }
    }
})

// Exporting actions for dispatching within functional components
export const { setBookmarks, setSelectedCategory, setFavoritesOnly, setSearchText, triggerRefresh } = BookmarkState.actions;

// Exporting the primary reducer for store registration
export default BookmarkState.reducer;  