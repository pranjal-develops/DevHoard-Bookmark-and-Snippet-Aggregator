import { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import UrlForm from '../components/UrlForm';
import Card from '../components/Card';
import axios from 'axios';
import Search from '../components/Search';
import Navbar from '../components/Navbar';
import Toast from '../components/Toast';


interface Bookmark {
    id: string;
    title: string;
    description: string;
    imgUrl: string;
    originalUrl: string;
    categories: string[];
    isFavorite: boolean;
}

export default function App() {
    const [url, setUrl] = useState('');
    const [isDark, setIsDark] = useState<boolean>(true);
    const [bookmarks, setBookmarks] = useState<Bookmark[]>([]);
    const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
    const [isSidebarOpen, setIsSidebarOpen] = useState<boolean>(() =>
        typeof window !== 'undefined' && window.matchMedia('(max-width:600px)').matches ? false : true
    );
    const [showToast, setShowToast] = useState(false);
    const [category, setCategory] = useState<string>("");
    const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
    const [favoritesOnly, setFavoritesOnly] = useState(false);
    const [refreshSignal, setRefreshSignal] = useState(false);


    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        try {
            await axios.post("http://localhost:8080/api/bookmarks", { url, category });
            setShowToast(true);

            setTimeout(() => {
                setRefreshSignal(val => !val);
            }, 3000);

            setTimeout(() => {
                setShowToast(false);
                setRefreshSignal(val => !val);;
            }, 10000);

            setUrl("");
            setCategory("");
            // window.location.reload();
        } catch (error) {
            console.log("Error saving bookmark", error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleTagClick = (tagName: string) => {
        setSelectedCategory(tagName);
    };


    return (
        <div className={`${isDark ? 'dark' : ''} h-screen overflow-hidden font-sans`}>
            <Navbar
                isDark={isDark}
                setIsDark={setIsDark}
                setBookmarks={setBookmarks}
                isSubmitting={isSubmitting}
                toggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)}
                selectedCategory={selectedCategory}
                favoritesOnly={favoritesOnly}
                refreshSignal={refreshSignal}
            />
            <div className="flex h-full bg-zinc-50 text-zinc-900 transition-colors duration-300 dark:bg-[hsl(0,0%,3%)] dark:text-zinc-100">

                <Sidebar
                    isOpen={isSidebarOpen}
                    setIsOpen={setIsSidebarOpen}
                    bookmarks={bookmarks}
                    selectedCategory={selectedCategory}
                    setSelectedCategory={setSelectedCategory}
                    favoritesOnly={favoritesOnly}
                    setFavoritesOnly={setFavoritesOnly}
                />

                {/* Main Content Area */}
                <main className="flex-1 flex flex-col items-center bg-zinc-50 dark:bg-[#020202] transition-colors duration-300 overflow-y-auto">
                    <div className="w-full max-w-[1600px] flex flex-col items-center pt-16 pb-24 px-6 md:px-12">

                        <div className="flex flex-col items-center mb-16 select-none">
                            <h1 className="text-3xl font-black text-zinc-900 dark:text-white tracking-widest uppercase font-mono">
                                Collect_<span className="text-lime-500">Bookmarks</span>
                            </h1>
                            <div className="flex items-center gap-1 mt-2">
                                <div className="h-1 w-12 bg-lime-500 shadow-[0_0_10px_rgba(132,204,22,0.5)]"></div>
                                <div className="h-1 w-1 bg-lime-500 animate-pulse"></div>
                            </div>
                        </div>

                        <UrlForm url={url} setUrl={setUrl} handleSubmit={handleSubmit} isSubmitting={isSubmitting} category={category} setCategory={setCategory} />

                        {/* Results Grid */}
                        <div className="w-full grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                            {bookmarks.map((b: Bookmark) => (
                                <Card key={b.id} bookmark={b} setBookmarks={setBookmarks} onTagClick={handleTagClick} />
                            ))}
                        </div>
                    </div>
                </main>
            </div>
            {showToast && (
                <Toast />
            )}
        </div>
    );
}
