import TagInput from './TagInput'; // Component for managing the tag collection associated with the new entry
import { useBookmarks } from '../hooks/useBookmarks'; // Custom hook for form submission and global state logic

/**
 * Primary URL Ingestion Form.
 * Facilitates the submission of new web resources to the archival backend.
 * Orchestrates the input of raw URLs and associated metadata tags.
 */
const UrlForm = () => {

    /**
     * Extraction of form-specific state and submission handlers.
     * Business logic is abstracted into the useBookmarks hook to ensure centralized lifecycle management.
     */
    const { url, setUrl, handleSubmit, categories, setCategories, isSubmitting } = useBookmarks()

    return (
        /* 
         * Form Layout: 
         * Implements a responsive flex container (stacking on mobile, inline on desktop).
         * Triggers the archival 'scrape' operation on submission.
         */
        <form onSubmit={handleSubmit} className="w-full max-w-2xl flex flex-col sm:flex-row gap-3 mb-12">
            
            {/* Source URL Input: Standardized URL validation and thematic mono-fonts */}
            <input
                type="url"
                required
                placeholder="Paste a URL (e.g. https://spring.io)"
                className="flex-1 px-5 py-4 rounded-xl bg-white dark:bg-[hsl(0,0%,10%)] border border-zinc-300 dark:border-zinc-700 focus:outline-none focus:ring-4 focus:ring-lime-500/20 dark:focus:ring-lime-400/20 focus:border-lime-500 dark:focus:border-lime-400 shadow-sm transition-all font-mono text-sm"
                value={url}
                onChange={(e) => setUrl(e.target.value)}
            />

            {/* Tag Orchestration: Integrated input for category classification */}
            <TagInput tags={categories} setTags={setCategories} placeHolder="Category (e.g. AI, Docs, Dev)" inForm={true} />

            {/* 
             * Submission Trigger:
             * Dynamically renders a pulsing 'Scraping' state while the asynchronous backend task is active.
             */
            isSubmitting ? (
                    <button
                        type="submit"
                        className="px-8 py-4 bg-lime-500 hover:bg-lime-400 text-black font-bold tracking-wider rounded-xl shadow-[0_0_15px_rgba(5,150,105,0.4)] hover:shadow-[0_0_25px_rgba(5,150,105,0.6)] transition-all active:scale-95 uppercase text-sm animate-pulse"
                    >
                        Scraping...
                    </button>
                ) : (
                    <button
                        type="submit"
                        className="px-8 py-4 bg-lime-500 hover:bg-lime-400 text-white dark:text-black font-bold tracking-wider rounded-xl shadow-[0_0_15px_rgba(5,150,105,0.4)] hover:shadow-[0_0_25px_rgba(5,150,105,0.6)] transition-all active:scale-95 uppercase text-sm"
                    >
                        Initialize
                    </button>
                )
            }
        </form>
    )
}

export default UrlForm