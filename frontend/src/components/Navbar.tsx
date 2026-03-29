import React from 'react'
import Search from './Search'

interface Bookmark {
    id: string;
    title: string;
    description: string;
    imgUrl: string;
    originalUrl: string;
    category: string;
    isFavorite: boolean;
}

interface NavbarProps {
    isDark: boolean;
    setIsDark: (value: boolean) => void;
    setBookmarks: (value: React.SetStateAction<Bookmark[]>) => void;
    isSubmitting: boolean;
    toggleSidebar: () => void;
    selectedCategory: string | null;
    favoritesOnly: boolean;
    refreshSignal: boolean;
    // setFavoritesOnly: (value: boolean) => void;
}

const Navbar: React.FC<NavbarProps> = ({ isDark, setIsDark, setBookmarks, isSubmitting, toggleSidebar, selectedCategory, favoritesOnly, refreshSignal }) => {
    const [isMobileSearchOpen, setIsMobileSearchOpen] = React.useState(false);

    return (
        <header className="w-full sticky top-0 z-50 transition-all duration-300">
            <div className="w-full flex items-center justify-between px-4 md:px-8 py-3 bg-white/80 dark:bg-black">
                {/* Logo Section */}
                <div className="flex items-center gap-2 md:gap-4 shrink-0">
                    <button
                        onClick={toggleSidebar}
                        className="p-2 -ml-2 text-zinc-600 dark:text-lime-400 hover:bg-zinc-100 dark:hover:bg-lime-500/10 rounded-lg transition-all active:scale-95"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><line x1="3" y1="12" x2="21" y2="12" /><line x1="3" y1="6" x2="21" y2="6" /><line x1="3" y1="18" x2="21" y2="18" /></svg>
                    </button>
                    <div className="flex items-center gap-2 group cursor-default">
                        <div className="w-1 md:w-2 h-5 md:h-6 bg-lime-500 shadow-[0_0_10px_rgba(132,204,22,0.5)]"></div>
                        <span className="font-mono font-black text-lg md:text-2xl tracking-tighter text-zinc-900 dark:text-white uppercase transition-all duration-300">
                            Dev<span className="text-lime-500 group-hover:animate-pulse">Hoard</span>
                        </span>
                    </div>
                </div>

                {/* Search Bar (Desktop/Tablet) */}
                <div className="hidden sm:block flex-1 max-w-sm md:max-w-md mx-4">
                    <Search
                        setBookmarks={setBookmarks}
                        isSubmitting={isSubmitting}
                        selectedCategory={selectedCategory}
                        favoritesOnly={favoritesOnly}
                        refreshSignal={refreshSignal}
                    />

                </div>

                {/* Actions Section */}
                <div className="flex items-center gap-1 md:gap-4 shrink-0">
                    <button
                        onClick={() => setIsMobileSearchOpen(!isMobileSearchOpen)}
                        className="sm:hidden p-2 text-zinc-500 dark:text-lime-400 hover:bg-zinc-100 dark:hover:bg-lime-500/10 rounded-lg transition-all"
                    >
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><circle cx="11" cy="11" r="8" /><line x1="21" y1="21" x2="16.65" y2="16.65" /></svg>
                    </button>

                    <div className="hidden xl:block text-[10px] font-mono text-zinc-400 dark:text-zinc-600 uppercase tracking-widest leading-none">
                        Auth_status:
                        <span className="text-lime-500 block">Guest</span>
                    </div>

                    <button
                        onClick={() => setIsDark(!isDark)}
                        className="p-2 md:px-4 md:py-2 rounded-lg font-mono text-[10px] font-bold uppercase tracking-widest border border-zinc-200 dark:border-lime-500/30 text-zinc-500 dark:text-lime-400 hover:bg-zinc-100 dark:hover:bg-lime-500/10 transition-all active:scale-95 shadow-[0_0_10px_rgba(132,204,22,0.05)]">
                        <span className="hidden md:inline">{isDark ? '// EXIT_CYBER' : '// ENTER_CYBER'}</span>
                        <span className="md:hidden">
                            {isDark ? (
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="5" /><line x1="12" y1="1" x2="12" y2="3" /><line x1="12" y1="21" x2="12" y2="23" /><line x1="4.22" y1="4.22" x2="5.64" y2="5.64" /><line x1="18.36" y1="18.36" x2="19.78" y2="19.78" /><line x1="1" y1="12" x2="3" y2="12" /><line x1="21" y1="12" x2="23" y2="12" /><line x1="4.22" y1="19.78" x2="5.64" y2="18.36" /><line x1="18.36" y1="5.64" x2="19.78" y2="4.22" /></svg>
                            ) : (
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" /></svg>
                            )}
                        </span>
                    </button>
                </div>
            </div>

            {/* Mobile Search Overlay */}
            {isMobileSearchOpen && (
                <div className="sm:hidden w-full px-4 py-3 bg-white/95 dark:bg-black/95 backdrop-blur-md border-b border-zinc-200 dark:border-zinc-800 animate-in slide-in-from-top duration-300">
                    <Search setBookmarks={setBookmarks}
                        isSubmitting={isSubmitting}
                        selectedCategory={selectedCategory}
                        favoritesOnly={favoritesOnly}
                        refreshSignal={refreshSignal} />
                </div>
            )}
        </header>
    )
}

export default Navbar