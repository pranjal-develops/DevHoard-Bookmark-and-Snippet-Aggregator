import { useState } from 'react';
import axios from 'axios';
import { useDispatch, useSelector } from 'react-redux';
import { type RootState } from '../store';
import { setShowToast } from '../store/slices/uiSlice';
import { setSelectedCategory, setSearchText, triggerRefresh } from '../store/slices/bookmarksSlice';

export const useBookmarks = () => {
    const guestId = localStorage.getItem('guestId');
    const dispatch = useDispatch();
    const { searchText } = useSelector((state: RootState) => state.bookmarks);
    const { token } = useSelector((state: RootState) => state.auth);
    const [url, setUrl] = useState('');
    const [categories, setCategories] = useState<string[]>([]);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        try {
            await axios.post("http://localhost:8080/api/bookmarks", { url, categories, guestId }, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
            dispatch(setShowToast(true));
            setTimeout(() => dispatch(triggerRefresh()), 3000);
            setTimeout(() => {
                dispatch(setShowToast(false));
                dispatch(triggerRefresh());
            }, 10000);
            setTimeout(() => dispatch(triggerRefresh()), 30000);
            setUrl("");
            setCategories([]);
        } catch (error) {
            console.log("Submission Error:", error);
        } finally {
            setIsSubmitting(false);
        }
    };

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
