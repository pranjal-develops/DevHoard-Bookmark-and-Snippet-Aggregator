import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { type RootState } from '../store';
import { setBookmarks } from '../store/slices/bookmarksSlice';
import api from '../api/api';

export const useFetchData = () => {
    const dispatch = useDispatch();
    const { selectedCategory, favoritesOnly, searchText, updateSignal } = useSelector((state: RootState) => state.bookmarks);
    const { token, isAuthenticated } = useSelector((state: RootState) => state.auth);

    useEffect(() => {
        const fetchData = async () => {
            const guestId = localStorage.getItem('guestId');
            try {
                const response = await api.get('/bookmarks', {
                    params: {
                        q: searchText,
                        guestId: guestId,
                        category: selectedCategory || undefined,
                        favoritesOnly: favoritesOnly || undefined
                    },
                    headers: token ? { Authorization: `Bearer ${token}` } : {}
                });
                dispatch(setBookmarks(response.data));
            } catch (error) {
                console.error("Fetch failure:", error);
            }
        };
        fetchData();
    }, [searchText, selectedCategory, favoritesOnly, token, dispatch, isAuthenticated, updateSignal]);
};


