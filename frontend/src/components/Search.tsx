import axios from 'axios';
import React, { useEffect, useState } from 'react'

interface SearchProps {
    setBookmarks: (value: React.SetStateAction<any[]>) => void;
}

const Search: React.FC<SearchProps> = ({ setBookmarks }) => {

    const [searchText, setSearchText] = useState<string>("");

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/api/bookmarks?q=${searchText}`);
                console.log(response.data);

                setBookmarks(response.data);
            } catch (error) {
                console.log(error);
            }
        }
        fetchData();
    }, [searchText])

    return (
        <input
            type="text"
            placeholder="Search bookmarks by title..."
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="w-full max-w-2xl px-5 py-3 mb-6 rounded-xl border border-slate-300 focus:ring-4 focus:ring-blue-500/20"
        />
    )
}

export default Search