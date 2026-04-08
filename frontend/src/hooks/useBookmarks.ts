import { useState, useEffect } from 'react';
import axios from 'axios';
import { useDispatch, useSelector } from 'react-redux';
import { type RootState } from '../store';
import { setShowToast } from '../store/slices/uiSlice';
import { setBookmarks, setSelectedCategory, setSearchText } from '../store/slices/bookmarksSlice';

export const useBookmarks = () => {
    const dispatch = useDispatch();
    const { selectedCategory, favoritesOnly, searchText } = useSelector((state: RootState) => state.bookmarks);
    const { token } = useSelector((state: RootState) => state.auth);
    const [url, setUrl] = useState('');
    const [categories, setCategories] = useState<string[]>([]);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [refreshSignal, setRefreshSignal] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                let queryUrl = `http://localhost:8080/api/bookmarks?q=${searchText}`;
                if (selectedCategory) queryUrl += `&category=${selectedCategory}`;
                if (favoritesOnly) queryUrl += `&favoritesOnly=${favoritesOnly}`;

                const response = await axios.get(queryUrl, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
                dispatch(setBookmarks(response.data));
            } catch (error) {
                console.log("Fetch Error:", error);
            }
        };
        fetchData();
    }, [searchText, selectedCategory, favoritesOnly, refreshSignal, dispatch, token]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        try {
            await axios.post("http://localhost:8080/api/bookmarks", { url, categories }, { headers: token ? { Authorization: `Bearer ${token}` } : {} });
            dispatch(setShowToast(true));
            setTimeout(() => setRefreshSignal(val => !val), 3000);
            setTimeout(() => {
                dispatch(setShowToast(false));
                setRefreshSignal(val => !val);
            }, 10000);
            setTimeout(() => setRefreshSignal(val => !val), 30000);
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
