import React from 'react' // React primitives for component definition
import TagInput from '../TagInput'; // Component for managing the tag/category collection
import { useBookmarks } from '../../hooks/useBookmarks'; // Global hook for navigation and search logic

/**
 * Interface definition for CardData properties.
 * Bridges the gap between raw entity metadata and the interactive UI elements.
 */
interface CardDataProps {
    title: string; // The primary title of the bookmarked resource
    description: string; // Brief metadata summary of the target content
    categories: string[]; // List of assigned classification tags
    setCategories: (tags: string[]) => void; // State-synching callback for tag mutations
    isEditing: boolean; // Flag status for the current interaction mode
    setIsEditing: (isEditing: boolean) => void; // Callback to toggle between viewing and meta-editing
}

/**
 * CardData Component.
 * Orchestrates the rendering and lifecycle management of bookmark metadata.
 * Provides the interactive layer for tag classification and editing.
 */
const CardData: React.FC<CardDataProps> = ({ title, description, categories, setCategories, isEditing, setIsEditing }) => {
    
    // Selection of global tag-click logic for navigation triggers
    const { handleTagClick } = useBookmarks();

    return (
        /* Data Fragment: Container for metadata summaries and identity tags */
        <>
            {/* 
             * Category HUD (Heads-Up Display): 
             * Dynamically manages the tag collection through the TagInput component.
             */
            }
            <div className="flex items-center justify-between mb-3 min-h-[24px]">
                <div className="flex flex-wrap gap-1.5">
                    <TagInput 
                        tags={categories} 
                        setTags={setCategories} 
                        placeHolder='Add Categories' 
                        isEditing={isEditing} 
                        onTagClick={handleTagClick} 
                    />
                </div>

                {/* 
                 * Edit Cycle Trigger:
                 * Dispatches an event-guard (stopPropagation) to ensure interaction is isolated from card navigation.
                 * Dynamically toggles between 'Edit/Append' and 'Close/Cancel' visual states.
                 */
                }
                <button
                    onClick={(e) => { e.stopPropagation(); setIsEditing(!isEditing); }}
                    className="text-zinc-500 hover:text-lime-500 transition-colors"
                    aria-label={isEditing ? "Terminate Editing Mode" : "Initialize Tag Mutation"}
                >
                    {isEditing ?
                        /* Terminate/Cancel UI Indicator */
                        <p className='text-xs font-mono uppercase text-zinc-400 dark:text-zinc-600 tracking-tighter'>x</p>
                        :
                        /* Initialization UI Indicator (Plus Icon) */
                        <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"><path d="M12 5v14M5 12h14" /></svg>
                    }
                </button>
            </div>

            {/* Core Metadata: Title rendering with fallback for unresolvable resources */}
            <h3 className="font-bold text-sm leading-snug line-clamp-2 text-zinc-900 dark:text-zinc-100 group-hover:text-lime-500 transition-colors">
                {title || "UNTITLED_STORAGE_NODE"}
            </h3>

            {/* Core Metadata: Description rendering with line-clamping and thematic fallback text */}
            <p className="text-zinc-500 dark:text-zinc-500 text-[11px] leading-relaxed line-clamp-2 mb-6 h-8">
                {description || "The archival system encountered zero metadata description for this memory fragment."}
            </p>
        </>
    )
}

export default CardData