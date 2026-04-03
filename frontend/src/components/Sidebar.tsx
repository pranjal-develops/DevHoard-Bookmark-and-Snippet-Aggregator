import { useDispatch, useSelector } from "react-redux";
import { toggleSidebar } from '../store/slices/uiSlice';
import type { RootState } from "../store";


interface SidebarProps {
    bookmarks: any[]; // New prop to see the categories
    selectedCategory: string | null; // Current filter
    setSelectedCategory: (category: string | null) => void; // Filter setter
    favoritesOnly: boolean;
    setFavoritesOnly: (value: boolean) => void;
}


const Sidebar: React.FC<SidebarProps> = ({ bookmarks, selectedCategory, setSelectedCategory, favoritesOnly, setFavoritesOnly }) => {

    const categories = Array.from(new Set(bookmarks.flatMap(b => b.categories || [])));
    const { isSidebarOpen } = useSelector((state: RootState) => state.ui);
    const dispatch = useDispatch();

    return (
        <>
            {/* Mobile/Overlay Backdrop */}
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
                    <span className="font-mono font-black">MENU_SYS</span>
                    <button onClick={() => dispatch(toggleSidebar())} className="text-zinc-500 hover:text-white">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18" /><line x1="6" y1="6" x2="18" y2="18" /></svg>
                    </button>
                </div>

                <nav className="flex-1 px-4 py-4 space-y-1">
                    <div className="px-4 mt-8 mb-4 text-[10px] font-bold uppercase tracking-widest text-zinc-400 dark:text-zinc-600">
                        Collections_Node
                    </div>
                    <button
                        onClick={() => setSelectedCategory(null)}
                        className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg font-mono text-xs transition-all ${!selectedCategory ? 'text-lime-500 bg-lime-500/10' : 'text-zinc-500 hover:text-white'}`}
                    >
                        [ ALL_COLLECTIONS ]
                    </button>

                    <button
                        onClick={() => {
                            setFavoritesOnly(!favoritesOnly);
                            setSelectedCategory(null); // Clear category when viewing favorites
                        }}
                        className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg font-mono text-xs transition-all 
    ${favoritesOnly ? 'bg-red-500/10 text-red-500 border-red-500/30' : 'text-zinc-500 hover:text-white'}`}
                    >
                        FAVORITES_RECORDS
                    </button>


                    {categories.map((cat) => (
                        <button
                            key={cat}
                            onClick={() => setSelectedCategory(cat)}
                            className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg font-mono text-xs transition-all ${selectedCategory === cat ? 'text-lime-500 bg-lime-500/10' : 'text-zinc-500 hover:text-white'}`}
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