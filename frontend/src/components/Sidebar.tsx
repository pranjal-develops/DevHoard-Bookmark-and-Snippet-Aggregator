import { useDispatch, useSelector } from "react-redux";
import { toggleSidebar } from '../store/slices/uiSlice';
import type { RootState } from "../store";
import { setSelectedCategory, setFavoritesOnly } from '../store/slices/bookmarksSlice';

const Sidebar = () => {
    const { items, selectedCategory, favoritesOnly } = useSelector((state: RootState) => state.bookmarks);
    const { isSidebarOpen } = useSelector((state: RootState) => state.ui);
    const dispatch = useDispatch();

    // Derive unique categories from items
    const categories = Array.from(new Set(items.flatMap(b => b.categories || [])));

    return (
        <>
            {isSidebarOpen && (
                <div
                    className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 lg:hidden"
                    onClick={() => dispatch(toggleSidebar())}
                />
            )}

            <aside
                className={`fixed lg:relative inset-y-0 left-0 bg-white dark:bg-black text-zinc-900 dark:text-zinc-400 min-h-screen transition-all duration-300 z-50 overflow-hidden 
                ${isSidebarOpen ? 'w-64 translate-x-0' : 'w-0 lg:w-0 -translate-x-full lg:translate-x-0'}`}
            >
                <div className="p-8 flex items-center justify-between lg:hidden text-lime-500">
                    <span className="font-mono font-black border-l-2 border-lime-500 pl-2">MENU_SYSTEM</span>
                    <button onClick={() => dispatch(toggleSidebar())} className="text-zinc-500 hover:text-white transition-colors">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" /></svg>
                    </button>
                </div>

                <nav className="flex-1 px-4 py-4 space-y-1">
                    <div className="px-4 mt-8 mb-4 text-[10px] font-bold uppercase tracking-widest text-zinc-400 dark:text-zinc-600">
                        Collections_Node
                    </div>

                    <button
                        onClick={() => {
                            dispatch(setSelectedCategory(null));
                            dispatch(setFavoritesOnly(false));
                        }}
                        className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg font-mono text-xs transition-all ${!selectedCategory && !favoritesOnly ? 'text-lime-500 bg-lime-500/10' : 'text-zinc-500 hover:text-white dark:hover:bg-zinc-900'}`}
                    >
                        [ ALL_COLLECTIONS ]
                    </button>

                    <button
                        onClick={() => {
                            dispatch(setFavoritesOnly(!favoritesOnly));
                            dispatch(setSelectedCategory(null)); 
                        }}
                        className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg font-mono text-xs transition-all 
                                    ${favoritesOnly ? 'bg-red-500/10 text-red-500 border-red-500/30' : 'text-zinc-500 hover:text-white dark:hover:bg-zinc-900'}`}
                    >
                        _FAVORITES_RECORDS
                    </button>

                    {categories.map((cat) => (
                        <button
                            key={cat}
                            onClick={() => dispatch(setSelectedCategory(cat))}
                            className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg font-mono text-xs transition-all ${selectedCategory === cat ? 'text-lime-500 bg-lime-500/10' : 'text-zinc-500 hover:text-white dark:hover:bg-zinc-900'}`}
                        >
                            :: {cat.toUpperCase()}
                        </button>
                    ))}
                </nav>
            </aside>
        </>
    )
}

export default Sidebar
