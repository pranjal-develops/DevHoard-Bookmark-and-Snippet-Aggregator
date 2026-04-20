import Sidebar from '../components/Sidebar'; // Sidebar navigation for filtering and account actions
import UrlForm from '../components/UrlForm'; // Primary input form for new bookmark submission
import Card from '../components/Card'; // Representation of a single bookmark entity
import Navbar from '../components/Navbar'; // Top-level navigation and branding
import Toast from '../components/Toast'; // Floating notification system for background tasks
import { useSelector } from 'react-redux'; // Redux state selector hook
import { type RootState } from '../store'; // Root state type for type-safe selections
import AuthModal from '../components/AuthModal'; // Modal for registration and authentication flows
import { useFetchData } from '../hooks/useFetchData'; // Reactive hook for data synchronization

/**
 * Domain model for Bookmark entities within the view layer.
 */
interface Bookmark {
    id: string;
    title: string;
    description: string;
    imgUrl: string;
    originalUrl: string;
    categories: string[];
    isFavorite: boolean;
}

/**
 * Main Application Shell.
 * Orchestrates the primary UI layout and initializes core data-fetching lifecycles.
 */
export default function App() {
    // Selection of visual state variables from the UI slice
    const { isDark, showToast } = useSelector((state: RootState) => state.ui);
    
    // Selection of the hydrated bookmark collection from the core slice
    const { items } = useSelector((state: RootState) => state.bookmarks);
    
    /**
     * Initialization of the reactive fetch hook.
     * Manages automatic data retrieval based on global search and filter state changes.
     */
    useFetchData();

    return (
        <div className={`${isDark ? 'dark' : ''} h-screen overflow-hidden font-sans`}>
            {/* Navigation and Identity components */}
            <Navbar />
            <AuthModal />

            {/* Primary Layout Wrapper: Flex-row foundation for Sidebar and Main Content */}
            <div className="flex h-full bg-zinc-50 text-zinc-900 transition-colors duration-300 dark:bg-[hsl(0,0%,3%)] dark:text-zinc-100">

                <Sidebar />

                {/* Main Content Area: Scrollable container for forms and bookmark grids */}
                <main className="flex-1 flex flex-col items-center bg-zinc-50 dark:bg-[#020202] transition-colors duration-300 overflow-y-auto">
                    <div className="w-full max-w-[1600px] flex flex-col items-center pt-16 pb-24 px-6 md:px-12">

                        {/* Visual Branding Section */}
                        <div className="flex flex-col items-center mb-16 select-none">
                            <h1 className="text-3xl font-black text-zinc-900 dark:text-white tracking-widest uppercase font-mono">
                                Collect_<span className="text-lime-500">Bookmarks</span>
                            </h1>
                            <div className="flex items-center gap-1 mt-2">
                                <div className="h-1 w-12 bg-lime-500 shadow-[0_0_10px_rgba(132,204,22,0.5)]"></div>
                                <div className="h-1 w-1 bg-lime-500 animate-pulse"></div>
                            </div>
                        </div>

                        {/* Input Interaction Point */}
                        <UrlForm />

                        {/* Dynamic Results Grid: Responsive column scaling (1 on mobile, 4 on extra-large screens) */}
                        <div className="w-full grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                            {items.map((b: Bookmark) => (
                                <Card key={b.id} bookmark={b} />
                            ))}
                        </div>
                    </div>
                </main>
            </div>

            {/* Notification Portal: Signals background processing state to the user */}
            {showToast && (
                <Toast />
            )}
        </div>
    );
}

