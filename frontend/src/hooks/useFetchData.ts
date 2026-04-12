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
                let queryUrl = `http://localhost:8080/api/bookmarks?q=${searchText}&guestId=${guestId}`;
                if (selectedCategory) queryUrl += `&category=${selectedCategory}`;
                if (favoritesOnly) queryUrl += `&favoritesOnly=${favoritesOnly}`;

                const response = await api.get(queryUrl, {
                    headers: token ? { Authorization: `Bearer ${token}` } : {}
                });
                dispatch(setBookmarks(response.data));
            } catch (error) {
                console.log("Fetch Error:", error);
            }
        };
        fetchData();
    }, [searchText, selectedCategory, favoritesOnly, token, dispatch, isAuthenticated, updateSignal]);
};
