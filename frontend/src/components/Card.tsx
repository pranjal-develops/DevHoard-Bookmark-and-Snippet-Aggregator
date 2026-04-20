import { useBookmarkActions } from '../hooks/useBookmarkActions' // Custom hook for business logic delegation
import CardVisuals from './Card/CardVisuals'; // Component for image and visual representation
import CardData from './Card/CardData'; // Component for title, description, and category metadata
import CardActions from './Card/CardActions'; // Component for user interaction triggers (Favorite, Delete, etc.)

/**
 * Interface definition for the Bookmark domain within the view layer.
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
 * Prop type definition for the Card component.
 */
interface CardProps {
  bookmark: Bookmark;
}

/**
 * Main Bookmark Card Component.
 * Acts as a container for individual bookmark metadata and interaction layers.
 * Implements a composition pattern to isolate visual, data, and action concerns.
 */
const Card = ({ bookmark }: CardProps) => {

  /**
   * Orchestration of bookmark-specific mutations.
   * Logic is encapsulated within the useBookmarkActions hook to maintain component-level purity.
   */
  const {
    Categories,
    setCategories,
    handleDelete,
    handleToggleFavorite,
    isEditing,
    setIsEditing
  } = useBookmarkActions(bookmark);

  return (
    /* 
     * Container: Implements dynamic thematic shifts and hover-based visual feedback.
     * Click interaction redirects the user to the source URL in a specialized context (new tab).
     */
    <div className="group relative bg-white dark:bg-[#0a0a0a] rounded-xl shadow-md border border-zinc-200 dark:border-zinc-800/50 overflow-hidden hover:shadow-[0_0_30px_rgba(163,230,53,0.15)] hover:border-lime-500/50 transition-all duration-500 cursor-pointer"
      onClick={() => window.open(bookmark.originalUrl, '_blank')}
    >
      {/* Visual Identity Layer */}
      <CardVisuals imgUrl={bookmark.imgUrl} title={bookmark.title} />

      {/* Metadata and Control Layer */}
      <div className="p-5">
        <CardData 
          title={bookmark.title} 
          description={bookmark.description} 
          categories={Categories} 
          setCategories={setCategories} 
          isEditing={isEditing} 
          setIsEditing={setIsEditing} 
        />
        
        <CardActions 
          id={bookmark.id} 
          originalUrl={bookmark.originalUrl} 
          onToggleFavorite={handleToggleFavorite} 
          isFavorite={bookmark.isFavorite} 
          onDelete={handleDelete} 
        />
      </div>
    </div>
  )
}

export default Card