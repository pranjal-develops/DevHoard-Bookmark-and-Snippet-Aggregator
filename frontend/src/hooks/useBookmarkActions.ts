import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios';
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

    const handleDelete = async (id: string) => {
        try {
            await axios.delete(`http://localhost:8080/api/bookmarks/${id}`, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
            dispatch(setBookmarks(items.filter(b => b.id !== id)));
        } catch (e) {
            console.log("Error deleting bookmark", e);
        }
    }

    const handleToggleFavorite = async () => {
        try {
            //We aren't sending any "body" data for a favorite toggle, so we must pass null or {} as the second argument.
            const response = await axios.patch(`http://localhost:8080/api/bookmarks/${bookmark.id}/favorite`, null, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
            dispatch(setBookmarks(items.map(b => b.id === bookmark.id ? response.data : b)));
        } catch (e) {
            console.log("Error toggling favorite", e);
        }
    };


    useEffect(() => {
        const saveCategories = async () => {
            try {
                const response = await axios.patch(`http://localhost:8080/api/bookmarks/${bookmark.id}/category`, {
                    categories: Categories
                }, { headers: token ? { Authorization: `Bearer ${token}` } : {} })
                dispatch(setBookmarks(items.map(b => b.id === bookmark.id ? response.data : b)));
            }
            catch (e) {
                console.log("Error saving Categories", e);
            }
        }

        if (isFirstRender.current) {
            isFirstRender.current = false;
            return;
        }
        else saveCategories();
    }, [Categories])

    return { Categories, setCategories, handleDelete, handleToggleFavorite, isEditing, setIsEditing }
}