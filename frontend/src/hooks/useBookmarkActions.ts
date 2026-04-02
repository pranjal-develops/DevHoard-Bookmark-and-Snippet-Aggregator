import React, { useEffect, useRef, useState } from 'react'
import axios from 'axios';

interface Bookmark {
    id: string;
    title: string;
    description: string;
    imgUrl: string;
    originalUrl: string;
    categories: string[];
    isFavorite: boolean;
}

export const useBookmarkActions = (bookmark: Bookmark, setBookmarks: (value: React.SetStateAction<Bookmark[]>) => void) => {

    const isFirstRender = useRef(true);
    const [Categories, setCategories] = useState(bookmark.categories || []);
    const [isEditing, setIsEditing] = React.useState(false);

    const handleDelete = async (id: string) => {
        try {
            await axios.delete(`http://localhost:8080/api/bookmarks/${id}`);
            setBookmarks(initialBookmarks => initialBookmarks.filter(b => b.id !== id));
        } catch (e) {
            console.log("Error deleting bookmark", e);
        }
    }

    const handleToggleFavorite = async () => {
        try {
            const response = await axios.patch(`http://localhost:8080/api/bookmarks/${bookmark.id}/favorite`);
            setBookmarks(initialBookmarks => initialBookmarks.map(b => b.id === bookmark.id ? response.data : b));
        } catch (e) {
            console.log("Error toggling favorite", e);
        }
    };


    useEffect(() => {
        const saveCategories = async () => {
            try {
                const response = await axios.patch(`http://localhost:8080/api/bookmarks/${bookmark.id}/category`, {
                    categories: Categories
                })
                setBookmarks(initialVal => initialVal.map(b => b.id === bookmark.id ? response.data : b));
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