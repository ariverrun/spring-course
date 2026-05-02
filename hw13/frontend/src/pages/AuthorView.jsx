import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';

function AuthorView() {
  const { id } = useParams();
  const [author, setAuthor] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`/api/v1/author/${id}`)
      .then(response => response.json())
      .then(data => {
        setAuthor(data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error:', error);
        setLoading(false);
      });
  }, [id]);

  if (loading) return <div>Загрузка...</div>;
  if (!author) return <div>Автор не найден</div>;

  return (
    <div>
      <h2>{author.fullName}</h2>
      <p><strong>ID:</strong> {author.id}</p>
      
      <Link to="/authors">Назад к списку</Link> |{' '}
      <Link to={`/authors/${id}/edit`}>Редактировать</Link>
    </div>
  );
}

export default AuthorView;