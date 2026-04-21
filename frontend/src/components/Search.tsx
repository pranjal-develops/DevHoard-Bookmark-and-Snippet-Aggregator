import { useBookmarks } from '../hooks/useBookmarks';

const Search = () => {
    const { searchText, setSearchText } = useBookmarks();

    return (
        <input
            type="text"
            placeholder="Search bookmarks by title..."
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="w-full px-5 py-2 rounded-xl bg-zinc-100 dark:bg-black border border-zinc-200 dark:border-zinc-800 focus:outline-none focus:ring-2 focus:ring-lime-500/20 focus:border-lime-500 dark:focus:border-lime-400 transition-all font-mono text-xs dark:text-zinc-300"
            aria-label="Search"
        />
    )
}

export default Search
