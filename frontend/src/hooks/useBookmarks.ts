import { useState } from 'react'; // React state hook for form data management
import api from '../api/api'; // Centralized Axios instance for authenticated HTTP requests
import { useDispatch, useSelector } from 'react-redux'; // Redux hooks for action dispatching and state selection
import { type RootState } from '../store'; // Type definition for the global application state
import { setShowToast } from '../store/slices/uiSlice'; // UI action for user feedback notifications
import { setSelectedCategory, setSearchText, triggerRefresh } from '../store/slices/bookmarksSlice'; // Core business logic actions

/**
 * Custom hook for orchestrating bookmark submission and high-level list interactions.
 * Encapsulates form state management and the coordination of background scraper refreshes.
 */
export const useBookmarks = () => {
    
    // Identity resolution for anonymous guests
    const guestId = localStorage.getItem('guestId');
    const dispatch = useDispatch();
    
    // Selection of current search state from Redux
    const { searchText } = useSelector((state: RootState) => state.bookmarks);
    const { token } = useSelector((state: RootState) => state.auth); // JWT for authenticated requests
    
    // Local form state for new bookmark submissions
    const [url, setUrl] = useState('');
    const [categories, setCategories] = useState<string[]>([]);
    const [isSubmitting, setIsSubmitting] = useState(false);

    /**
     * Handles the submission of the bookmark form.
     * Triggers a remote scrape operation and initiates a multi-stage refresh cycle 
     * to account for asynchronous backend processing.
     */
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        
        try {
            // Execution: Sending the scrape request to the backend orchestrator
            await api.post("/bookmarks", { url, categories, guestId }, { 
                headers: token ? { Authorization: `Bearer ${token}` } : {} 
            });
            
            // Interaction Feedback: Activating the 'Processing' toast notification
            dispatch(setShowToast(true));
            
            /**
             * Multi-Stage Polling/Refresh Strategy:
             * Since scraping is @Async on the backend, the document may not be ready immediately.
             * We trigger sequential refreshes to progressively hydrate the UI as tasks complete.
             */
            setTimeout(() => dispatch(triggerRefresh()), 3000); // Pulse 1: Quick scrape check
            
            setTimeout(() => {
                dispatch(setShowToast(false)); // Cleanup notification
                dispatch(triggerRefresh()); // Pulse 2: Median scrape check
            }, 10000);
            
            setTimeout(() => dispatch(triggerRefresh()), 30000); // Pulse 3: Deep-scrape fallback check
            
            // Cleanup: Resetting form state for subsequent entries
            setUrl("");
            setCategories([]);
        } catch (error) {
            console.error("[Bookmarks Hook] submission lifecycle failure:", error);
        } finally {
            setIsSubmitting(false);
        }
    };

    /**
     * Facilitates tag-based navigation by updating the global category filter.
     */
    const handleTagClick = (tagName: string) => {
        dispatch(setSelectedCategory(tagName));
    };

    return {
        url, setUrl,
        categories, setCategories,
        searchText, setSearchText: (text: string) => dispatch(setSearchText(text)),
        isSubmitting,
        handleSubmit,
        handleTagClick
    };
};

