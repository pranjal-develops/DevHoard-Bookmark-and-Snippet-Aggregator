import Sidebar from '../components/Sidebar';
import UrlForm from '../components/UrlForm';
import Card from '../components/Card';
import Navbar from '../components/Navbar';
import Toast from '../components/Toast';
import { useSelector } from 'react-redux';
import { type RootState } from '../store';

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
    const { isDark, showToast } = useSelector((state: RootState) => state.ui);
    const { items } = useSelector((state: RootState) => state.bookmarks);

    return (
        <div className={`${isDark ? 'dark' : ''} h-screen overflow-hidden font-sans`}>
            <Navbar />
            <div className="flex h-full bg-zinc-50 text-zinc-900 transition-colors duration-300 dark:bg-[hsl(0,0%,3%)] dark:text-zinc-100">

                <Sidebar />

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

                        <UrlForm />

                        {/* Results Grid */}
                        <div className="w-full grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                            {items.map((b: Bookmark) => (
                                <Card key={b.id} bookmark={b} />
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
