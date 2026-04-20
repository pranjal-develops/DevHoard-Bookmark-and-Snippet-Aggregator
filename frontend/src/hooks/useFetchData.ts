import { useEffect } from 'react'; // React effect hook for side-effect management
import { useDispatch, useSelector } from 'react-redux'; // Redux hooks for state access and mutations
import { type RootState } from '../store'; // Type definition for the central state store
import { setBookmarks } from '../store/slices/bookmarksSlice'; // Action for updating the local bookmark cache
import api from '../api/api'; // Centralized Axios instance with base configuration

/**
 * Custom hook to manage the reactive fetching of bookmark data.
 * Synchronizes the remote data state with local Redux state whenever filter criteria or session tokens change.
 */
export const useFetchData = () => {
    const dispatch = useDispatch();
    
    // Selecting reactive filter criteria and the refresh signal from Redux
    const { selectedCategory, favoritesOnly, searchText, updateSignal } = useSelector((state: RootState) => state.bookmarks);
    const { token, isAuthenticated } = useSelector((state: RootState) => state.auth);

    useEffect(() => {
        /**
         * Asynchronous fetch operation.
         * Constructs the query based on current filters and dispatches results to the store.
         */
        const fetchData = async () => {
            const guestId = localStorage.getItem('guestId');
            try {
                // Resolution: Utilizing the centralized api instance for relative path mapping
                const response = await api.get('/bookmarks', {
                    params: {
                        q: searchText,
                        guestId: guestId,
                        category: selectedCategory || undefined, // Only include if truthy
                        favoritesOnly: favoritesOnly || undefined
                    },
                    headers: token ? { Authorization: `Bearer ${token}` } : {}
                });
                
                // State Sync: Updating the global bookmark registry with the fresh dataset
                dispatch(setBookmarks(response.data));
            } catch (error) {
                console.error("[Fetch Hook] Failed to synchronize bookmark data:", error);
            }
        };
        
        fetchData();
        
        /* 
         * Dependency Array Analysis:
         * We refetch whenever search text changes, a category is selected, favorites are toggled, 
         * or when an external 'updateSignal' (e.g. from a successful post) is triggered.
         */
    }, [searchText, selectedCategory, favoritesOnly, token, dispatch, isAuthenticated, updateSignal]);
};

