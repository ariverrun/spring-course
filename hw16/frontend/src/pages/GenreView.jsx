import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';

function GenreView() {
  const { id } = useParams();
  const [genre, setGenre] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`/api/v1/genre/${id}`)
      .then(response => response.json())
      .then(data => {
        setGenre(data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error:', error);
        setLoading(false);
      });
  }, [id]);

  if (loading) return <div>Загрузка...</div>;
  if (!genre) return <div>Жанр не найден</div>;

  return (
    <div>
      <h2>{genre.name}</h2>
      <p><strong>ID:</strong> {genre.id}</p>
      
      <Link to="/genres">Назад к списку</Link> |{' '}
      <Link to={`/genres/${id}/edit`}>Редактировать</Link>
    </div>
  );
}

export default GenreView;