import { useBookmarks } from '../hooks/useBookmarks'; // Custom hook for centralized search and bookmark state orchestration

/**
 * Global Search Component.
 * Provides a text-based interface for filtering the bookmark collection in real-time.
 * Integrates with the useBookmarks hook to propagate search criteria to the reactive data layers.
 */
const Search = () => {

    /**
     * Extraction of search-specific state and dispatchers.
     * Logic is centralized within useBookmarks to ensure consistency across the UI (Navbar, Mobile Overlay).
     */
    const { searchText, setSearchText } = useBookmarks();

    return (
        /* 
         * Input Field: Implements a reactive binding pattern.
         * Thematic styling is aligned with the 'Cyber-Minimalist' aesthetic (Mono fonts, subtle borders).
         */
        <input
            type="text"
            placeholder="Search bookmarks by title..."
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            className="w-full px-5 py-2 rounded-xl bg-zinc-100 dark:bg-black border border-zinc-200 dark:border-zinc-800 focus:outline-none focus:ring-2 focus:ring-lime-500/20 focus:border-lime-500 dark:focus:border-lime-400 transition-all font-mono text-xs dark:text-zinc-300"
            aria-label="Filter bookmarks by keyword"
        />
    )
}

export default Search