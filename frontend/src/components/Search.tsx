import axios from 'axios';
import React, { useEffect, useState } from 'react'

interface SearchProps {
    setBookmarks: (value: React.SetStateAction<any[]>) => void;
    isSubmitting: boolean;
    selectedCategory: string | null;
    favoritesOnly: boolean;
}

const Search: React.FC<SearchProps> = ({ setBookmarks, isSubmitting, selectedCategory, favoritesOnly }) => {

    const [searchText, setSearchText] = useState<string>("");

    useEffect(() => {
        const fetchData = async () => {
            try {
                let url = `http://localhost:8080/api/bookmarks?q=${searchText}`;
                if (selectedCategory) {
                    url += `&category=${selectedCategory}`;
                }
                if (favoritesOnly) {
                    url += `&favoritesOnly=${favoritesOnly}`;
                }
                const response = await axios.get(url);
                setBookmarks(response.data);
            } catch (error) {
                console.log(error);
            }
        }
        fetchData();
    }, [searchText, isSubmitting, selectedCategory, favoritesOnly])

    return (
        <input
            type="text"
            placeholder="Search bookmarks by title..."
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="w-full px-5 py-2 rounded-xl bg-zinc-100 dark:bg-black border border-zinc-200 dark:border-zinc-800 focus:outline-none focus:ring-2 focus:ring-lime-500/20 focus:border-lime-500 dark:focus:border-lime-400 transition-all font-mono text-xs dark:text-zinc-300"
        />
    )
}

export default Search