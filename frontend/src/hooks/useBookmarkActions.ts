import React, { useEffect, useRef, useState } from 'react'
import api from '../api/api';
import { useDispatch, useSelector } from 'react-redux';
import type { RootState } from '../store';
import { setBookmarks } from '../store/slices/bookmarksSlice';

interface Bookmark {
    id: string;
    title: string;
    description: string;
    imgUrl: string;
    originalUrl: string;
    categories: string[];
    isFavorite: boolean;
}

export const useBookmarkActions = (bookmark: Bookmark) => {
    const dispatch = useDispatch();
    const { items } = useSelector((state: RootState) => state.bookmarks);
    const { token } = useSelector((state: RootState) => state.auth);

    const isFirstRender = useRef(true);
    const [Categories, setCategories] = useState(bookmark.categories || []);
    const [isEditing, setIsEditing] = React.useState(false);
    const guestId = localStorage.getItem('guestId');

    const handleDelete = async (id: string) => {
        try {
            await api.delete(`/bookmarks/${id}?guestId=${guestId}`, {
                headers: token ? { Authorization: `Bearer ${token}` } : {}
            });
            dispatch(setBookmarks(items.filter(b => b.id !== id)));
        } catch (e) {
            console.error("Deletion failed:", e);
        }
    }

    const handleToggleFavorite = async () => {
        try {
            const response = await api.patch(`/bookmarks/${bookmark.id}/favorite`, { guestId }, { 
                headers: token ? { Authorization: `Bearer ${token}` } : {} 
            });
            dispatch(setBookmarks(items.map(b => b.id === bookmark.id ? response.data : b)));
        } catch (e) {
            console.error("Toggle favorite failed:", e);
        }
    };

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
                console.error("Category sync failed:", e);
            }
        }

        // Guard against effect running on mount
        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }
        saveCategories();
    }, [Categories])

    return { Categories, setCategories, handleDelete, handleToggleFavorite, isEditing, setIsEditing }
}

