import React, { useEffect, useRef, useState } from 'react' // React hooks for local state and lifecycle management
import api from '../api/api'; // Centralized Axios instance with global interceptors
import { useDispatch, useSelector } from 'react-redux'; // Redux hooks for state management
import type { RootState } from '../store'; // RootState type for selector orchestration
import { setBookmarks } from '../store/slices/bookmarksSlice'; // Action to update bookmarks in global state

/**
 * Interface definition for the Bookmark domain model in the frontend context.
 */
interface Bookmark {
    id: string;
    title: string;
    description: string;
    imgUrl: string;
    originalUrl: string;
    categories: string[];
    isFavorite: boolean;
}

/**
 * Custom hook to encapsulate bookmark-related mutation logic.
 * Manages local UI state for categories and editing modes while orchestrating remote synchronization.
 */
export const useBookmarkActions = (bookmark: Bookmark) => {

    const dispatch = useDispatch();
    const { items } = useSelector((state: RootState) => state.bookmarks); // Current collection for optimistic updates
    const { token } = useSelector((state: RootState) => state.auth); // JWT credential from global state

    /**
     * Ref used as a guard to prevent the 'saveCategories' effect from firing on the initial component mount.
     */
    const isFirstRender = useRef(true);

    // Local state for immediate UI feedback on category changes and editing toggles
    const [Categories, setCategories] = useState(bookmark.categories || []);
    const [isEditing, setIsEditing] = React.useState(false);

    /**
     * Retrieval of the unique identity token for guest session management.
     */
    const guestId = localStorage.getItem('guestId');

    /**
     * Handles the asynchronous deletion of a bookmark entity.
     * Propagates identity via query parameters to accommodate DELETE method constraints.
     */
    const handleDelete = async (id: string) => {
        try {
            // Since delete requests don't have a body, we pass the guestId as a query parameter.
            await api.delete(`/bookmarks/${id}?guestId=${guestId}`, {
                headers: token ? { Authorization: `Bearer ${token}` } : {}
            });

            // Synchronizing Redux state by filtering out the deleted entity
            dispatch(setBookmarks(items.filter(b => b.id !== id)));
        } catch (e) {
            console.error("[Bookmark Actions] Persistence failure during deletion:", e);
        }
    }

    /**
     * Toggles the 'favorite' status of a bookmark.
     * Uses a PATCH request to perform a partial state update on the server.
     */
    const handleToggleFavorite = async () => {
        try {
            //We aren't sending any "body" data for a favorite toggle, so we must pass null or {} as the second argument.
            // const response = await axios.patch(`http://localhost:8080/api/bookmarks/${bookmark.id}/favorite`, null, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
            //We are now passing guestId as body
            const response = await api.patch(`/bookmarks/${bookmark.id}/favorite`, { guestId }, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
            dispatch(setBookmarks(items.map(b => b.id === bookmark.id ? response.data : b)));
        } catch (e) {
            console.error("[Bookmark Actions] State synchronization failure for favorite toggle:", e);
        }
    };

    /**
     * Effect hook to synchronize tag/category changes with the backend.
     * Implementation includes a debounce/mount guard via isFirstRender.
     */
    useEffect(() => {
        const saveCategories = async () => {
            try {
                const response = await api.patch(`/bookmarks/${bookmark.id}/category`, {
                    categories: Categories,
                    guestId
                }, {
                    headers: token ? { Authorization: `Bearer ${token}` } : {}
                })

                dispatch(setBookmarks(items.map(b => b.id === bookmark.id ? response.data : b)));
            }
            catch (e) {
                console.error("[Bookmark Actions] Remote synchronization failed for categories:", e);
            }
        }

        // Logic check: Only initiate remote save if this change was triggered by user interaction post-mount
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }
        else saveCategories();
    }, [Categories])

    return { Categories, setCategories, handleDelete, handleToggleFavorite, isEditing, setIsEditing }
}
