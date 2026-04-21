import { useBookmarkActions } from '../hooks/useBookmarkActions'
import CardVisuals from './Card/CardVisuals';
import CardData from './Card/CardData';
import CardActions from './Card/CardActions';

interface Bookmark {
  id: string;
  title: string;
  description: string;
  imgUrl: string;
  originalUrl: string;
  categories: string[];
  isFavorite: boolean;
}

interface CardProps {
  bookmark: Bookmark;
}

const Card = ({ bookmark }: CardProps) => {
  const {
    Categories,
    setCategories,
    handleDelete,
    handleToggleFavorite,
    isEditing,
    setIsEditing
  } = useBookmarkActions(bookmark);

  return (
    <div className="group relative bg-white dark:bg-[#0a0a0a] rounded-xl shadow-md border border-zinc-200 dark:border-zinc-800/50 overflow-hidden hover:shadow-[0_0_30px_rgba(163,230,53,0.15)] hover:border-lime-500/50 transition-all duration-500 cursor-pointer"
      onClick={() => window.open(bookmark.originalUrl, '_blank')}
    >
      <CardVisuals imgUrl={bookmark.imgUrl} title={bookmark.title} />

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
